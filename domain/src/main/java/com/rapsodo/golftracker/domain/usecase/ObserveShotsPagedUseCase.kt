package com.rapsodo.golftracker.domain.usecase

import androidx.paging.PagingData
import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Paged shot stream for [playerId], filtered by [equipment] (null = all clubs). */
class ObserveShotsPagedUseCase @Inject constructor(
    private val repo: ShotRepository,
) {
    operator fun invoke(playerId: String, equipment: String?): Flow<PagingData<Shot>> =
        repo.observeShotsPaged(playerId, equipment)
}
