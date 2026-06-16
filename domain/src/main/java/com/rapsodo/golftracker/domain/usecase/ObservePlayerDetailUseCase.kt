package com.rapsodo.golftracker.domain.usecase

import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observes a single [Player] by ID from the local Room cache. */
class ObservePlayerDetailUseCase @Inject constructor(
    private val repo: PlayerRepository,
) {
    operator fun invoke(playerId: String): Flow<Player?> =
        repo.observePlayer(playerId)
}
