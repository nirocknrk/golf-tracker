package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.repository.ShotRepository
import javax.inject.Inject

/** Forces a full refresh of all shots for [playerId] from the network. */
class SyncPlayerShotsUseCase @Inject constructor(
    private val repo: ShotRepository,
) {
    suspend operator fun invoke(playerId: String) = repo.syncShots(playerId)
}
