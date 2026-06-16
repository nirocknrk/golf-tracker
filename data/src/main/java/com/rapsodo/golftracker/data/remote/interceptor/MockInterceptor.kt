package com.rapsodo.golftracker.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import javax.inject.Inject

/**
 * OkHttp interceptor that short-circuits all HTTP calls with hardcoded JSON.
 *
 * Active when [BuildConfig.USE_MOCK_API] == true.
 * Swap out by setting USE_MOCK_API=false in gradle.properties and supplying a real base URL.
 *
 * Routing logic mirrors [GolfApiService] endpoint paths.
 */
class MockInterceptor @Inject constructor() : Interceptor {

    private val json = "application/json; charset=utf-8".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        // Guard: pass through to real network when mock is disabled.
        // BuildConfig.USE_MOCK_API is set per build variant in :data/build.gradle.kts.
        if (!com.rapsodo.golftracker.data.BuildConfig.USE_MOCK_API) {
            return chain.proceed(chain.request())
        }

        val request  = chain.request()
        val url      = request.url
        val path     = url.encodedPath            // e.g. "/players" or "/players/p1/shots"
        val page     = url.queryParameter("page")?.toIntOrNull() ?: 1
        val perPage  = url.queryParameter("per_page")?.toIntOrNull() ?: 20
        val querySearch = url.queryParameter("q")

        Timber.d("MockInterceptor → $path [page=$page, perPage=$perPage]")

        // Route: GET /players/{id}/shots
        val shotsMatch = Regex("/players/([^/]+)/shots").find(path)
        if (shotsMatch != null) {
            val playerId = shotsMatch.groupValues[1]
            // GolfApiService.getShots uses @Query("club") for equipment filter
            val shotClubFilter = url.queryParameter("club")
            return mockResponse(chain, buildShotsJson(playerId, page, perPage, shotClubFilter))
        }

        // Route: GET /players/{id}
        val playerMatch = Regex("/players/([^/]+)$").find(path)
        if (playerMatch != null) {
            val playerId = playerMatch.groupValues[1]
            val player = ALL_PLAYERS.firstOrNull { it["id"] == playerId }
                ?: return errorResponse(chain, 404, """{"error":"Player not found"}""")
            return mockResponse(chain, playerToJson(player))
        }

        // Route: GET /players
        // GolfApiService.getPlayers sends club filter as @Query("club_id") — not "club"
        if (path.endsWith("/players")) {
            val playerClubId = url.queryParameter("club_id")
            return mockResponse(chain, buildPlayersJson(page, perPage, querySearch, playerClubId))
        }

        return errorResponse(chain, 404, """{"error":"Unknown mock path: $path"}""")
    }

    // ── JSON builders ──────────────────────────────────────────────────────

    private fun buildPlayersJson(page: Int, perPage: Int, query: String?, clubId: String?): String {
        var filtered = ALL_PLAYERS
        if (!query.isNullOrBlank())  filtered = filtered.filter { it["name"]!!.contains(query, ignoreCase = true) }
        if (!clubId.isNullOrBlank()) filtered = filtered.filter { it["clubId"] == clubId }
        return pagedJson(filtered, page, perPage) { playerToJson(it) }
    }

    private fun buildShotsJson(
        playerId: String, page: Int, perPage: Int, club: String?
    ): String {
        val shots = SHOTS_BY_PLAYER[playerId] ?: emptyList()
        val filtered = if (club.isNullOrBlank()) shots
                       else shots.filter { it["club"] == club }
        return pagedJson(filtered, page, perPage) { shotToJson(it) }
    }

    private fun <T> pagedJson(
        all: List<T>, page: Int, perPage: Int, toJson: (T) -> String
    ): String {
        val totalPages = if (all.isEmpty()) 1 else ((all.size - 1) / perPage) + 1
        val start = (page - 1) * perPage
        val items = if (start >= all.size) emptyList() else all.subList(start, minOf(start + perPage, all.size))
        val dataArray = items.joinToString(",\n    ") { toJson(it) }
        return """
            {
              "data": [$dataArray],
              "total": ${all.size},
              "page": $page,
              "per_page": $perPage,
              "total_pages": $totalPages
            }
        """.trimIndent()
    }

    private fun playerToJson(p: Map<String, String>) = """
        {
          "id": "${p["id"]}",
          "name": "${p["name"]}",
          "initials": "${p["initials"]}",
          "avatar_url": ${p["avatarUrl"]?.let { "\"$it\"" } ?: "null"},
          "club_id": "${p["clubId"]}",
          "club_name": "${p["clubName"]}",
          "club_short": "${p["clubShort"]}",
          "handed": "${p["handed"]}",
          "handicap": ${p["handicap"]},
          "avg_ball_speed": ${p["avgBallSpeed"]},
          "avg_carry": ${p["avgCarry"]},
          "avg_launch_angle": ${p["avgLaunchAngle"]},
          "avg_spin_rate": ${p["avgSpinRate"]},
          "avg_smash_factor": ${p["avgSmashFactor"]},
          "top_speed": ${p["topSpeed"]},
          "longest_drive": ${p["longestDrive"]},
          "shot_count": ${p["shotCount"]}
        }""".trimIndent()

    private fun shotToJson(s: Map<String, String>) = """
        {
          "id": "${s["id"]}",
          "player_id": "${s["playerId"]}",
          "seq": ${s["seq"]},
          "club": "${s["club"]}",
          "ball_speed": ${s["ballSpeed"]},
          "launch_angle": ${s["launchAngle"]},
          "carry": ${s["carry"]},
          "total": ${s["total"]},
          "spin_rate": ${s["spinRate"]},
          "smash_factor": ${s["smashFactor"]},
          "offline": ${s["offline"]},
          "apex": ${s["apex"]},
          "club_head_speed": ${s["clubHeadSpeed"] ?: "null"}
        }""".trimIndent()

    // ── HTTP response helpers ──────────────────────────────────────────────

    private fun mockResponse(chain: Interceptor.Chain, body: String) =
        Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(200).message("OK")
            .body(body.toResponseBody(json))
            .build()

    private fun errorResponse(chain: Interceptor.Chain, code: Int, body: String) =
        Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(code).message("Error")
            .body(body.toResponseBody(json))
            .build()

    // ── Seed data ──────────────────────────────────────────────────────────

    companion object {
        /** 20 golfer profiles across two clubs. */
        val ALL_PLAYERS: List<Map<String, String>> = listOf(
            player("p01","James Anderson","JA","club01","Pine Haven","PH","RIGHT",5.2,287.4,218.3,12.1,2840,1.48,301.2,231.5,142),
            player("p02","Sarah Mitchell","SM","club01","Pine Haven","PH","RIGHT",12.8,261.9,198.6,11.8,3120,1.43,278.4,210.2,98),
            player("p03","Michael Chen","MC","club01","Pine Haven","PH","LEFT",8.4,275.2,208.4,12.4,2960,1.46,291.8,224.7,127),
            player("p04","Emma Thompson","ET","club01","Pine Haven","PH","RIGHT",15.3,248.7,187.9,11.5,3240,1.41,265.1,198.3,73),
            player("p05","David Wilson","DW","club02","Oakridge GC","OG","RIGHT",3.1,296.8,225.4,12.8,2720,1.51,315.3,238.6,215),
            player("p06","Lisa Garcia","LG","club02","Oakridge GC","OG","RIGHT",18.7,239.4,181.2,10.9,3380,1.38,254.7,189.4,51),
            player("p07","Robert Taylor","RT","club02","Oakridge GC","OG","RIGHT",7.6,278.9,211.6,12.2,2890,1.47,295.1,226.8,163),
            player("p08","Jennifer Brown","JB","club01","Pine Haven","PH","RIGHT",10.2,269.3,203.7,12.0,3050,1.44,284.6,215.9,110),
            player("p09","William Davis","WD","club01","Pine Haven","PH","LEFT",4.8,289.7,221.8,12.6,2790,1.49,306.4,234.1,189),
            player("p10","Amanda Martinez","AM","club02","Oakridge GC","OG","RIGHT",13.5,255.8,194.1,11.6,3170,1.42,271.3,204.8,84),
            player("p11","Christopher Lee","CL","club02","Oakridge GC","OG","LEFT",6.9,281.4,213.8,12.3,2930,1.46,297.6,228.4,148),
            player("p12","Michelle White","MW","club01","Pine Haven","PH","RIGHT",16.1,244.6,184.8,11.3,3290,1.40,259.8,194.7,62),
            player("p13","Daniel Harris","DH","club02","Oakridge GC","OG","RIGHT",2.4,299.1,227.6,13.0,2690,1.52,318.7,241.3,234),
            player("p14","Jessica Clark","JC","club01","Pine Haven","PH","RIGHT",9.7,272.1,206.3,11.9,3010,1.45,288.9,218.6,121),
            player("p15","Thomas Lewis","TL","club02","Oakridge GC","OG","LEFT",11.4,263.7,200.1,11.7,3090,1.43,279.2,211.8,93),
            player("p16","Ashley Robinson","AR","club01","Pine Haven","PH","RIGHT",19.2,236.8,179.4,10.7,3420,1.37,251.3,187.2,44),
            player("p17","Kevin Walker","KW","club02","Oakridge GC","OG","RIGHT",1.7,303.4,230.9,13.2,2650,1.54,322.8,244.7,267),
            player("p18","Nicole Hall","NH","club01","Pine Haven","PH","RIGHT",14.8,251.2,190.4,11.4,3210,1.41,267.5,201.1,76),
            player("p19","Mark Young","MY","club02","Oakridge GC","OG","RIGHT",8.1,276.5,209.7,12.1,2870,1.47,293.4,222.9,138),
            player("p20","Stephanie King","SK","club01","Pine Haven","PH","LEFT",17.6,241.9,182.6,11.1,3340,1.39,256.4,192.1,55),
        )

        private fun player(
            id: String, name: String, initials: String,
            clubId: String, clubName: String, clubShort: String,
            handed: String, handicap: Double,
            avgBallSpeed: Double, avgCarry: Double, avgLaunch: Double,
            avgSpin: Int, avgSmash: Double, topSpeed: Double, longest: Double, shots: Int,
        ) = mapOf(
            "id" to id, "name" to name, "initials" to initials,
            "clubId" to clubId, "clubName" to clubName, "clubShort" to clubShort,
            "handed" to handed, "handicap" to "$handicap",
            "avgBallSpeed" to "$avgBallSpeed", "avgCarry" to "$avgCarry",
            "avgLaunchAngle" to "$avgLaunch", "avgSpinRate" to "$avgSpin",
            "avgSmashFactor" to "$avgSmash", "topSpeed" to "$topSpeed",
            "longestDrive" to "$longest", "shotCount" to "$shots",
        )

        /** 15 shots per player — sufficient for chart + recent list. */
        val SHOTS_BY_PLAYER: Map<String, List<Map<String, String>>> = buildMap {
            ALL_PLAYERS.forEach { player ->
                val playerId = player["id"]!!
                put(playerId, (1..15).map { seq ->
                    val base = player["avgBallSpeed"]!!.toDouble()
                    val carry = player["avgCarry"]!!.toDouble()
                    val clubs = listOf("Driver", "3 Wood", "5 Iron", "7 Iron", "Pitching Wedge")
                    val club  = clubs[(seq - 1) % clubs.size]
                    val speed = base + (-8..8).random()
                    val cDist = carry + (-15..15).random()
                    shot(
                        id = "${playerId}_s${seq.toString().padStart(2,'0')}",
                        playerId = playerId, seq = seq, club = club,
                        ballSpeed  = "%.1f".format(speed),
                        launch     = "%.1f".format(11.5 + (-1.5..1.5).random()),
                        carry      = "%.1f".format(cDist),
                        total      = "%.1f".format(cDist * 1.04),
                        spinRate   = "${player["avgSpinRate"]!!.toInt() + (-150..150).random()}",
                        smash      = "%.2f".format(player["avgSmashFactor"]!!.toDouble() + (-0.04..0.04).random()),
                        offline    = "%.1f".format((-12..12).random().toDouble()),
                        apex       = "%.1f".format(28.0 + (-5..5).random()),
                        chs        = "%.1f".format(speed / 1.49),
                    )
                })
            }
        }

        private fun shot(
            id: String, playerId: String, seq: Int, club: String,
            ballSpeed: String, launch: String, carry: String, total: String,
            spinRate: String, smash: String, offline: String, apex: String, chs: String,
        ) = mapOf(
            "id" to id, "playerId" to playerId, "seq" to "$seq", "club" to club,
            "ballSpeed" to ballSpeed, "launchAngle" to launch, "carry" to carry,
            "total" to total, "spinRate" to spinRate, "smashFactor" to smash,
            "offline" to offline, "apex" to apex, "clubHeadSpeed" to chs,
        )

        // Extension to generate random values in Int range (for mock data)
        private fun IntRange.random() = (Math.random() * (last - first + 1) + first).toInt()
        private fun ClosedFloatingPointRange<Double>.random() =
            Math.random() * (endInclusive - start) + start
    }
}
