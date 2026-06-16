package com.rapsodo.golftracker.data.remote.api

import com.rapsodo.golftracker.data.remote.dto.PagedResponse
import com.rapsodo.golftracker.data.remote.dto.PlayerDto
import com.rapsodo.golftracker.data.remote.dto.ShotDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for the Golf Performance Tracker REST API.
 *
 * Base URL is set in `:app/build.gradle.kts` via [BuildConfig.API_BASE_URL].
 * All endpoints are backed by [MockInterceptor] while [BuildConfig.USE_MOCK_API] is true.
 *
 * Pagination contract: 1-based [page], server-side [perPage] capped at 20.
 */
interface GolfApiService {

    /**
     * GET /players
     *
     * Query params:
     *   page     — 1-based page number
     *   per_page — items per page (default 20)
     *   q        — optional name search
     *   club_id  — optional club filter
     *   sort     — field name: ball_speed | carry | name (default: ball_speed)
     *   order    — asc | desc (default: desc)
     */
    @GET("players")
    suspend fun getPlayers(
        @Query("page")     page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("q")        query: String? = null,
        @Query("club_id")  clubId: String? = null,
        @Query("sort")     sort: String = "ball_speed",
        @Query("order")    order: String = "desc",
    ): PagedResponse<PlayerDto>

    /**
     * GET /players/{id}
     * Returns a single player — called on detail screen entry to ensure freshness.
     */
    @GET("players/{id}")
    suspend fun getPlayer(@Path("id") id: String): PlayerDto

    /**
     * GET /players/{id}/shots
     *
     * Query params:
     *   page     — 1-based page number
     *   per_page — items per page (default 20)
     *   club     — optional equipment filter (e.g. "Driver")
     */
    @GET("players/{id}/shots")
    suspend fun getShots(
        @Path("id")        playerId: String,
        @Query("page")     page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("club")     equipment: String? = null,
    ): PagedResponse<ShotDto>
}
