package com.rapsodo.golftracker.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rapsodo.golftracker.data.local.GolfDatabase
import com.rapsodo.golftracker.data.local.entity.RemoteKeyEntity
import com.rapsodo.golftracker.data.local.entity.ShotEntity
import com.rapsodo.golftracker.data.mapper.toEntities
import com.rapsodo.golftracker.data.remote.api.GolfApiService
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Paging 3 RemoteMediator for the shot list.
 *
 * Scoped per [playerId] + optional [equipment] filter.
 * Uses the same [RemoteKeyEntity] table as [PlayerRemoteMediator] — keys are
 * namespaced by a "shots_" prefix to avoid collisions.
 */
@OptIn(ExperimentalPagingApi::class)
class ShotRemoteMediator(
    private val playerId: String,
    private val equipment: String?,
    private val api: GolfApiService,
    private val db: GolfDatabase,
) : RemoteMediator<Int, ShotEntity>() {

    private val shotDao      = db.shotDao()
    private val remoteKeyDao = db.remoteKeyDao()
    private val queryKey     = "shots_${playerId}_${equipment ?: "all"}"

    override suspend fun initialize(): InitializeAction {
        val oldest = shotDao.oldestUpdatedAt(playerId) ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        val ageMs  = System.currentTimeMillis() - oldest
        val isStale = ageMs > TimeUnit.MINUTES.toMillis(CACHE_TTL_MINUTES)
        Timber.d("ShotMediator[$queryKey] cache age ${ageMs / 1000}s, stale=$isStale")
        return if (isStale) InitializeAction.LAUNCH_INITIAL_REFRESH
               else         InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ShotEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> FIRST_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND  -> {
                    val remoteKey = remoteKeyDao.getByKey(queryKey)
                    remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            Timber.d("ShotMediator[$queryKey] loading page=$page")
            val response = api.getShots(
                playerId  = playerId,
                page      = page,
                perPage   = state.config.pageSize,
                equipment = equipment,
            )

            val endOfPagination = page >= response.totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    shotDao.deleteByPlayer(playerId)
                    remoteKeyDao.deleteByKey(queryKey)
                }
                remoteKeyDao.upsert(
                    RemoteKeyEntity(
                        queryKey = queryKey,
                        prevKey  = if (page == FIRST_PAGE) null else page - 1,
                        nextKey  = if (endOfPagination) null else page + 1,
                    )
                )
                shotDao.upsertAll(response.data.toEntities())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: IOException) {
            Timber.e(e, "ShotMediator network error")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Timber.e(e, "ShotMediator HTTP error ${e.code()}")
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE        = 1
        private const val CACHE_TTL_MINUTES = 30L
    }
}
