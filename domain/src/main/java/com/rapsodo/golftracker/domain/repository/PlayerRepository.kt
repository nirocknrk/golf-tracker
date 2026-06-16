package com.rapsodo.golftracker.domain.repository

import androidx.paging.PagingData
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerFilter
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for player data.
 *
 * Implementations must satisfy:
 *   - Room is always the source for all emitted [Flow] values.
 *   - Network calls write to Room via [RemoteMediator]; UI never reads network directly.
 *   - [observePlayersPaged] returns a new [PagingData] stream per [filter] change.
 */
interface PlayerRepository {

    /**
     * Paged player stream filtered/sorted by [filter].
     *
     * Uses Paging 3 [RemoteMediator] for offline-first behaviour:
     *   - Room cache < 30 min → serves local data without network call.
     *   - Room cache stale or empty → mediator fetches from API and writes to Room.
     */
    fun observePlayersPaged(filter: PlayerFilter): Flow<PagingData<Player>>

    /** Single player detail observed from Room. Emits on every DB write. */
    fun observePlayer(playerId: String): Flow<Player?>

    /** Force a full refresh from the network for this [filter]. */
    suspend fun syncPlayers(filter: PlayerFilter)

    /**
     * Distinct clubs (clubId to clubShort) present in the local player cache.
     * Used to populate filter chips on the Players List screen.
     */
    fun observeAvailableClubs(): Flow<List<Pair<String, String>>>
}
