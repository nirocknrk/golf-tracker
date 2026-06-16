package com.rapsodo.golftracker.data.di

import android.content.Context
import androidx.room.Room
import com.rapsodo.golftracker.data.local.GolfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides [GolfDatabase] as an application-scoped singleton.
 *
 * [GolfDatabase] is the Single Source of Truth for all UI data in this app.
 * Neither ViewModels nor use cases ever read from the network directly —
 * everything flows through Room via:
 *   - [PlayerDao]   — player rows + club projections
 *   - [ShotDao]     — per-player shot rows
 *   - [RemoteKeyDao] — pagination cursor storage
 *
 * The two RemoteMediators ([PlayerRemoteMediator], [ShotRemoteMediator]) are
 * the only writers to [PlayerDao] and [ShotDao]. They always write inside
 * `db.withTransaction { }` so partial page writes are never committed.
 *
 * Schema evolution:
 *   - Bump [GolfDatabase.version] whenever you add/remove/rename columns.
 *   - Provide a [Migration] via `.addMigrations(...)` to preserve user data.
 *   - For development only, `fallbackToDestructiveMigration` drops the entire
 *     database on a schema mismatch — remove this for production.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideGolfDatabase(@ApplicationContext context: Context): GolfDatabase =
        Room.databaseBuilder(
            context,
            GolfDatabase::class.java,
            GolfDatabase.DATABASE_NAME,
        )
        // TODO: replace with proper migrations before production release.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
