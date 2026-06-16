package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.model.PlayerFilter
import com.rapsodo.golftracker.domain.repository.PlayerRepository
import javax.inject.Inject

/** Triggers an explicit network → Room refresh for the given [filter]. */
class SyncPlayersUseCase @Inject constructor(
    private val repo: PlayerRepository,
) {
    suspend operator fun invoke(filter: PlayerFilter) = repo.syncPlayers(filter)
}
