package com.rapsodo.golftracker.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rapsodo.golftracker.data.local.GolfDatabase
import com.rapsodo.golftracker.data.local.entity.PlayerEntity
import com.rapsodo.golftracker.data.local.entity.RemoteKeyEntity
import com.rapsodo.golftracker.data.mapper.toEntities
import com.rapsodo.golftracker.data.remote.api.GolfApiService
import com.rapsodo.golftracker.domain.model.PlayerFilter
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Paging 3 RemoteMediator for the player list.
 *
 * Responsibilities:
 *   - On REFRESH: decide whether to hit network (cache > 30 min) or serve Room.
 *   - On APPEND: fetch the next page and write to Room within a transaction.
 *   - On PREPEND: no-op (server-side pagination is forward-only).
 *
 * [filter] parameterises the query key so each filter combo has independent state.
 */
@OptIn(ExperimentalPagingApi::class)
class PlayerRemoteMediator(
    private val filter: PlayerFilter,
    private val api: GolfApiService,
    private val db: GolfDatabase,
) : RemoteMediator<Int, PlayerEntity>() {

    private val playerDao    = db.playerDao()
    private val remoteKeyDao = db.remoteKeyDao()
    private val queryKey     = filter.toCacheKey()

    override suspend fun initialize(): InitializeAction {
        val oldest = playerDao.oldestUpdatedAt() ?: return InitializeAction.LAUNCH_INITIAL_REFRESH
        val ageMs  = System.currentTimeMillis() - oldest
        val isStale = ageMs > TimeUnit.MINUTES.toMillis(CACHE_TTL_MINUTES)
        Timber.d("PlayerMediator[$queryKey] cache age ${ageMs / 1000}s, stale=$isStale")
        return if (isStale) InitializeAction.LAUNCH_INITIAL_REFRESH
               else         InitializeAction.SKIP_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PlayerEntity>,
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

            Timber.d("PlayerMediator[$queryKey] loading page=$page")
            val response = api.getPlayers(
                page    = page,
                perPage = state.config.pageSize,
                query   = filter.query,
                clubId  = filter.clubId,
                sort    = filter.sortBy.name.lowercase(),
                order   = if (filter.sortAscending) "asc" else "desc",
            )

            val endOfPagination = page >= response.totalPages

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    playerDao.deleteAll()
                    remoteKeyDao.deleteByKey(queryKey)
                }
                remoteKeyDao.upsert(
                    RemoteKeyEntity(
                        queryKey = queryKey,
                        prevKey  = if (page == FIRST_PAGE) null else page - 1,
                        nextKey  = if (endOfPagination) null else page + 1,
                    )
                )
                playerDao.upsertAll(response.data.toEntities())
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: IOException) {
            Timber.e(e, "PlayerMediator network error")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Timber.e(e, "PlayerMediator HTTP error ${e.code()}")
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE       = 1
        private const val CACHE_TTL_MINUTES = 30L
    }
}
