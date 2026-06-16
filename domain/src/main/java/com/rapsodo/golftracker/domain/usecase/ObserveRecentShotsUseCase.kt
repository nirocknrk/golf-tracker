package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Emits the [limit] most recent shots for [playerId] — drives the "Recent Shots" section. */
class ObserveRecentShotsUseCase @Inject constructor(
    private val repo: ShotRepository,
) {
    operator fun invoke(playerId: String, limit: Int = 5): Flow<List<Shot>> =
        repo.observeRecentShots(playerId, limit)
}
