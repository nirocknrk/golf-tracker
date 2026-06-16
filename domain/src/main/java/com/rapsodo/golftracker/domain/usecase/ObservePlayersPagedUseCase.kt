package com.rapsodo.golftracker.domain.usecase

import androidx.paging.PagingData
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerFilter
import com.rapsodo.golftracker.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Returns a [PagingData] stream of players matching [filter]. */
class ObservePlayersPagedUseCase @Inject constructor(
    private val repo: PlayerRepository,
) {
    operator fun invoke(filter: PlayerFilter): Flow<PagingData<Player>> =
        repo.observePlayersPaged(filter)
}
