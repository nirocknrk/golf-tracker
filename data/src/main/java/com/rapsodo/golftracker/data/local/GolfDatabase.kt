package com.rapsodo.golftracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rapsodo.golftracker.data.local.dao.PlayerDao
import com.rapsodo.golftracker.data.local.dao.RemoteKeyDao
import com.rapsodo.golftracker.data.local.dao.ShotDao
import com.rapsodo.golftracker.data.local.entity.PlayerEntity
import com.rapsodo.golftracker.data.local.entity.RemoteKeyEntity
import com.rapsodo.golftracker.data.local.entity.ShotEntity

/**
 * Room database — Single Source of Truth for the Golf Performance Tracker.
 *
 * Schema export path configured in `:data/build.gradle.kts` via KSP args.
 * Bump [version] and add a [Migration] whenever the schema changes.
 */
@Database(
    entities = [
        PlayerEntity::class,
        ShotEntity::class,
        RemoteKeyEntity::class,
    ],
    version = 2, // v2: removed RemoteKeyEntity.createdAt (dead field)
    exportSchema = true,
)
abstract class GolfDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun shotDao(): ShotDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val DATABASE_NAME = "golf_tracker.db"
    }
}
