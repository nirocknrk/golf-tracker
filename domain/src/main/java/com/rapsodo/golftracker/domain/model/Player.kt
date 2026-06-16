package com.rapsodo.golftracker.domain.model

/**
 * Core domain model for a golfer profile.
 *
 * All distance/speed units are metric:
 *   ballSpeed / topSpeed / clubHeadSpeed → km/h
 *   distances (carry, total)             → metres
 *   spinRate                             → rpm
 *   angles                               → degrees
 */
data class Player(
    val id: String,
    val name: String,
    /** Two-letter avatar initials derived from name on creation. */
    val initials: String,
    /** Remote avatar URL — nullable; fall back to initials avatar. */
    val avatarUrl: String?,

    // Club / organisation membership
    val clubId: String,
    val clubName: String,
    /** Short display label, e.g. "PH" for Pine Haven. */
    val clubShort: String,

    val handed: Handedness,
    val handicap: Double,

    // Aggregate performance metrics (computed by backend or [ComputePlayerStatsUseCase])
    val avgBallSpeed: Double,       // km/h
    val avgCarryDistance: Double,   // m
    val avgLaunchAngle: Double,     // °
    val avgSpinRate: Int,           // rpm
    val avgSmashFactor: Double,     // dimensionless (ball speed / club head speed)
    val topSpeed: Double,           // km/h — personal best ball speed
    val longestDrive: Double,       // m — personal best carry
    val totalShots: Int,
)
