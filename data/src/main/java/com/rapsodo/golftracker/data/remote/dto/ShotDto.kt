package com.rapsodo.golftracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Network representation of a single tracked shot. */
@JsonClass(generateAdapter = true)
data class ShotDto(
    @Json(name = "id")              val id: String,
    @Json(name = "player_id")       val playerId: String,
    @Json(name = "seq")             val seq: Int,
    @Json(name = "club")            val club: String,
    @Json(name = "ball_speed")      val ballSpeed: Double,
    @Json(name = "launch_angle")    val launchAngle: Double,
    @Json(name = "carry")           val carry: Double,
    @Json(name = "total")           val total: Double,
    @Json(name = "spin_rate")       val spinRate: Int,
    @Json(name = "smash_factor")    val smashFactor: Double,
    @Json(name = "offline")         val offline: Double,
    @Json(name = "apex")            val apex: Double,
    @Json(name = "club_head_speed") val clubHeadSpeed: Double?,
)
