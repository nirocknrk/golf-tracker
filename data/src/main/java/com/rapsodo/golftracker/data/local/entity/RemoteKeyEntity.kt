package com.rapsodo.golftracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Paging 3 remote key storage — one row per unique filter+sort combination.
 *
 * [queryKey] — stable cache key produced by [PlayerFilter.toCacheKey()] or
 *              the shot-list equivalent. Scopes remote keys to a specific page
 *              position *per filter*, so changing filters resets pagination
 *              independently for each filter combination.
 *
 * Cache TTL is NOT tracked here. It is checked via [PlayerDao.oldestUpdatedAt] /
 * [ShotDao.oldestUpdatedAt] on the data entities themselves, which are stamped
 * with [PlayerEntity.updatedAt] / [ShotEntity.updatedAt] on every upsert.
 * A [createdAt] field previously existed here but was never read — removed to
 * avoid a misleading dead field.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val queryKey: String,
    val prevKey: Int?,
    val nextKey: Int?,
)
