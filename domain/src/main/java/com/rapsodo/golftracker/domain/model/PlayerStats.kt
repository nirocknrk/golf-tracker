package com.rapsodo.golftracker.domain.model

/**
 * Derived statistics computed from a player's shot history by [ComputePlayerStatsUseCase].
 *
 * Displayed in the stats grid on the Player Detail screen.
 */
data class PlayerStats(
    val avgBallSpeed: Double,       // km/h
    val avgCarryDistance: Double,   // m
    val avgLaunchAngle: Double,     // °
    val avgSpinRate: Int,           // rpm
    val avgSmashFactor: Double,

    val topSpeed: Double,           // km/h
    val longestDrive: Double,       // m

    /** Carries ± std-deviation — used to size the DispersionChart ellipse. */
    val dispersionRadius: Double,   // m

    /** Ordered (seq asc) ball speeds for the BallSpeedTrendChart. */
    val ballSpeedSeries: List<Double>,

    val totalShots: Int,
)
