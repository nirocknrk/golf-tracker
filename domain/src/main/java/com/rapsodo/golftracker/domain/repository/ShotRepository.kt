package com.rapsodo.golftracker.domain.repository

import androidx.paging.PagingData
import com.rapsodo.golftracker.domain.model.Shot
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for shot data.
 *
 * All [Flow] emissions originate from Room; network writes go through [RemoteMediator].
 */
interface ShotRepository {

    /**
     * Paged shot stream for [playerId], optionally filtered by [equipment] label.
     * Null [equipment] = all clubs.
     */
    fun observeShotsPaged(playerId: String, equipment: String?): Flow<PagingData<Shot>>

    /** All shots for [playerId] — used by [ComputePlayerStatsUseCase]. */
    fun observeAllShots(playerId: String): Flow<List<Shot>>

    /** The [limit] most recent shots for [playerId] — shown in the "Recent Shots" section. */
    fun observeRecentShots(playerId: String, limit: Int = 5): Flow<List<Shot>>

    /** Distinct equipment labels present in the DB for [playerId] (drives filter chips). */
    fun observeAvailableClubs(playerId: String): Flow<List<String>>

    /** Force a full refresh of all shots for [playerId] from the network. */
    suspend fun syncShots(playerId: String)
}
