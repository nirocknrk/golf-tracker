package com.rapsodo.golftracker.domain.model

/**
 * A single tracked golf shot.
 *
 * Used in the DispersionChart (offline X vs carry Y) and BallSpeedTrendChart (seq → speed).
 *
 * Unit conventions (metric throughout):
 *   ballSpeed / clubHeadSpeed → km/h
 *   carryDistance / totalDistance / offline / apex → metres
 *   launchAngle → degrees
 *   spinRate → rpm
 *   smashFactor → dimensionless
 */
data class Shot(
    val id: String,
    val playerId: String,
    /** Chronological shot number within this player's session (1-based). */
    val seq: Int,
    /** Club / equipment label, e.g. "Driver", "7 Iron", "Pitching Wedge". */
    val equipment: String,

    val ballSpeed: Double,          // km/h
    val launchAngle: Double,        // °
    val carryDistance: Double,      // m
    val totalDistance: Double,      // m
    val spinRate: Int,              // rpm
    val smashFactor: Double,

    /** Lateral offline distance: negative = left, positive = right. */
    val offline: Double,            // m
    val apex: Double,               // m (max height)

    /** Optional — null if the device didn't capture club head speed. */
    val clubHeadSpeed: Double?,     // km/h
)
