package com.rapsodo.golftracker.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Network representation of a player — maps 1:1 to REST API response fields. */
@JsonClass(generateAdapter = true)
data class PlayerDto(
    @Json(name = "id")               val id: String,
    @Json(name = "name")             val name: String,
    @Json(name = "initials")         val initials: String,
    @Json(name = "avatar_url")       val avatarUrl: String?,
    @Json(name = "club_id")          val clubId: String,
    @Json(name = "club_name")        val clubName: String,
    @Json(name = "club_short")       val clubShort: String,
    @Json(name = "handed")           val handed: String,      // "RIGHT" | "LEFT"
    @Json(name = "handicap")         val handicap: Double,
    @Json(name = "avg_ball_speed")   val avgBallSpeed: Double,
    @Json(name = "avg_carry")        val avgCarry: Double,
    @Json(name = "avg_launch_angle") val avgLaunchAngle: Double,
    @Json(name = "avg_spin_rate")    val avgSpinRate: Int,
    @Json(name = "avg_smash_factor") val avgSmashFactor: Double,
    @Json(name = "top_speed")        val topSpeed: Double,
    @Json(name = "longest_drive")    val longestDrive: Double,
    @Json(name = "shot_count")       val shotCount: Int,
)
