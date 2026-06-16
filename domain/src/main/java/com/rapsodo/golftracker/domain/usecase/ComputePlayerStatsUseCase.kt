package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.model.PlayerStats
import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.sqrt

/**
 * Derives [PlayerStats] from the full shot list stored in Room.
 *
 * Pure computation — no network calls. Input is a Room Flow so the stats
 * update automatically whenever new shots are written to the DB.
 */
class ComputePlayerStatsUseCase @Inject constructor(
    private val repo: ShotRepository,
) {
    operator fun invoke(playerId: String): Flow<PlayerStats?> =
        repo.observeAllShots(playerId).map { shots ->
            if (shots.isEmpty()) return@map null
            val sorted = shots.sortedBy { it.seq }

            val avgBallSpeed      = sorted.map { it.ballSpeed }.average()
            val avgCarry          = sorted.map { it.carryDistance }.average()
            val avgLaunch         = sorted.map { it.launchAngle }.average()
            val avgSpin           = sorted.map { it.spinRate }.average().toInt()
            val avgSmash          = sorted.map { it.smashFactor }.average()
            val topSpeed          = sorted.maxOf { it.ballSpeed }
            val longestDrive      = sorted.maxOf { it.carryDistance }
            val dispersionRadius  = standardDeviation(sorted.map { it.offline })

            PlayerStats(
                avgBallSpeed      = avgBallSpeed,
                avgCarryDistance  = avgCarry,
                avgLaunchAngle    = avgLaunch,
                avgSpinRate       = avgSpin,
                avgSmashFactor    = avgSmash,
                topSpeed          = topSpeed,
                longestDrive      = longestDrive,
                dispersionRadius  = dispersionRadius,
                ballSpeedSeries   = sorted.map { it.ballSpeed },
                totalShots        = sorted.size,
            )
        }

    private fun standardDeviation(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val mean = values.average()
        val variance = values.sumOf { (it - mean) * (it - mean) } / values.size
        return sqrt(variance)
    }
}
