package com.rapsodo.golftracker.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.rapsodo.golftracker.data.local.GolfDatabase
import com.rapsodo.golftracker.data.mapper.toDomain
import com.rapsodo.golftracker.data.mapper.toEntities
import com.rapsodo.golftracker.data.remote.api.GolfApiService
import com.rapsodo.golftracker.data.remote.paging.PlayerRemoteMediator
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerFilter
import com.rapsodo.golftracker.domain.model.SortField
import com.rapsodo.golftracker.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PlayerRepositoryImpl @Inject constructor(
    private val db: GolfDatabase,
    private val api: GolfApiService,
) : PlayerRepository {

    private val playerDao = db.playerDao()

    override fun observePlayersPaged(filter: PlayerFilter): Flow<PagingData<Player>> {
        Timber.d("PlayerRepo observePlayersPaged filter=$filter")
        return Pager(
            config = PagingConfig(
                pageSize           = PAGE_SIZE,
                prefetchDistance   = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            remoteMediator      = PlayerRemoteMediator(filter, api, db),
            pagingSourceFactory = { pagingSourceFor(filter) },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun observePlayer(playerId: String): Flow<Player?> =
        playerDao.observeById(playerId).map { it?.toDomain() }

    /**
     * Full refresh — fetches ALL pages from the API and commits atomically.
     *
     * Fix: previously `deleteAll()` ran on page 1 before the remaining pages
     * were fetched, leaving the DB empty on any mid-loop crash. Now all pages
     * are fetched into memory first, then the delete + upsert run in a single
     * [withTransaction] so partial writes are never committed.
     */
    override suspend fun syncPlayers(filter: PlayerFilter) {
        Timber.d("PlayerRepo syncPlayers filter=$filter")
        try {
            val allEntities = buildList {
                var page = 1
                do {
                    val response = api.getPlayers(
                        page    = page,
                        perPage = PAGE_SIZE,
                        query   = filter.query,
                        clubId  = filter.clubId,
                        sort    = filter.sortBy.name.lowercase(),
                        order   = if (filter.sortAscending) "asc" else "desc",
                    )
                    addAll(response.data.toEntities())
                    page++
                } while (page <= response.totalPages)
            }

            db.withTransaction {
                playerDao.deleteAll()
                playerDao.upsertAll(allEntities)
            }
            Timber.d("PlayerRepo syncPlayers wrote ${allEntities.size} players")
        } catch (e: Exception) {
            Timber.e(e, "PlayerRepo syncPlayers failed")
            throw e
        }
    }

    override fun observeAvailableClubs(): Flow<List<Pair<String, String>>> =
        playerDao.observeAvailableClubs().map { list ->
            list.map { it.clubId to it.clubShort }
        }

    /**
     * Selects the most specific [PagingSource] for the active [filter].
     *
     * Priority order:
     *   1. Both query + clubId active → combined WHERE clause
     *      (Fix: previously only the query branch was taken, silently ignoring clubId)
     *   2. Query only → name search
     *   3. Club only → club filter
     *   4. Sort by CARRY_DISTANCE or NAME → dedicated sorted queries
     *   5. Default → sort by ball speed DESC
     */
    private fun pagingSourceFor(filter: PlayerFilter) = when {
        !filter.query.isNullOrBlank() && !filter.clubId.isNullOrBlank() ->
            playerDao.pagingSourceByQueryAndClub(filter.query!!, filter.clubId!!)
        !filter.query.isNullOrBlank() ->
            playerDao.pagingSourceByQuery(filter.query!!)
        !filter.clubId.isNullOrBlank() ->
            playerDao.pagingSourceByClub(filter.clubId!!)
        filter.sortBy == SortField.NAME ->
            playerDao.pagingSourceByName()
        filter.sortBy == SortField.CARRY_DISTANCE ->
            playerDao.pagingSourceByCarry()
        else ->
            playerDao.pagingSourceByBallSpeed()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
