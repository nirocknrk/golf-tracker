package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Emits distinct equipment labels for [playerId] — populates the filter-chip row. */
class ObserveAvailableClubsUseCase @Inject constructor(
    private val repo: ShotRepository,
) {
    operator fun invoke(playerId: String): Flow<List<String>> =
        repo.observeAvailableClubs(playerId)
}
