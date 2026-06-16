package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Returns distinct clubs (clubId → clubShort) from the local player cache.
 * Used to populate the club filter chips on the Players List screen.
 */
class ObserveAvailablePlayerClubsUseCase @Inject constructor(
    private val repository: PlayerRepository,
) {
    operator fun invoke(): Flow<List<Pair<String, String>>> =
        repository.observeAvailableClubs()
}
