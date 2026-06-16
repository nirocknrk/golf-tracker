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
import com.rapsodo.golftracker.data.remote.paging.ShotRemoteMediator
import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.repository.ShotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ShotRepositoryImpl @Inject constructor(
    private val db: GolfDatabase,
    private val api: GolfApiService,
) : ShotRepository {

    private val shotDao = db.shotDao()

    override fun observeShotsPaged(playerId: String, equipment: String?): Flow<PagingData<Shot>> {
        Timber.d("ShotRepo observeShotsPaged player=$playerId equipment=$equipment")
        return Pager(
            config = PagingConfig(
                pageSize           = PAGE_SIZE,
                prefetchDistance   = PAGE_SIZE / 2,
                enablePlaceholders = false,
            ),
            remoteMediator      = ShotRemoteMediator(playerId, equipment, api, db),
            pagingSourceFactory = {
                if (equipment != null) shotDao.pagingSourceByClub(playerId, equipment)
                else                   shotDao.pagingSource(playerId)
            },
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun observeAllShots(playerId: String): Flow<List<Shot>> =
        shotDao.observeAll(playerId).map { list -> list.map { it.toDomain() } }

    override fun observeRecentShots(playerId: String, limit: Int): Flow<List<Shot>> =
        shotDao.observeRecent(playerId, limit).map { list -> list.map { it.toDomain() } }

    override fun observeAvailableClubs(playerId: String): Flow<List<String>> =
        shotDao.observeAvailableClubs(playerId)

    /**
     * Full refresh — fetches ALL pages then commits atomically.
     *
     * Fix: previously `deleteByPlayer()` ran on page 1 before all remaining
     * pages were fetched, leaving the player's shots empty on any mid-loop
     * crash. Now pages are accumulated first, then the delete + upsert happen
     * in a single [withTransaction].
     */
    override suspend fun syncShots(playerId: String) {
        Timber.d("ShotRepo syncShots player=$playerId")
        try {
            val allEntities = buildList {
                var page = 1
                do {
                    val response = api.getShots(
                        playerId = playerId,
                        page     = page,
                        perPage  = PAGE_SIZE,
                    )
                    addAll(response.data.toEntities())
                    page++
                } while (page <= response.totalPages)
            }

            db.withTransaction {
                shotDao.deleteByPlayer(playerId)
                shotDao.upsertAll(allEntities)
            }
            Timber.d("ShotRepo syncShots wrote ${allEntities.size} shots for $playerId")
        } catch (e: Exception) {
            Timber.e(e, "ShotRepo syncShots failed")
            throw e
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
