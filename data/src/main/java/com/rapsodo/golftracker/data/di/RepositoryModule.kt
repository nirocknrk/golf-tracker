package com.rapsodo.golftracker.data.di

import com.rapsodo.golftracker.data.repository.PlayerRepositoryImpl
import com.rapsodo.golftracker.data.repository.ShotRepositoryImpl
import com.rapsodo.golftracker.domain.repository.PlayerRepository
import com.rapsodo.golftracker.domain.repository.ShotRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds concrete repository implementations to their domain interfaces.
 *
 * Why `@Binds` instead of `@Provides`:
 *   `@Binds` is purely declarative — Hilt generates the binding without
 *   instantiating the module at runtime. It requires the module to be abstract.
 *
 * How the dependency graph connects:
 *   [PlayerRepositoryImpl] is injected with:
 *     - [PlayerDao] + [RemoteKeyDao] + [GolfDatabase] (from [DatabaseModule])
 *     - [GolfApiService] (from [NetworkModule])
 *   It is then exposed as [PlayerRepository] to the domain layer use cases:
 *     - [ObservePlayersPagedUseCase]
 *     - [ObservePlayerDetailUseCase]
 *     - [ObserveAvailablePlayerClubsUseCase]
 *
 *   [ShotRepositoryImpl] follows the same pattern, exposed as [ShotRepository] to:
 *     - [ObserveShotsPagedUseCase]
 *     - [ObserveRecentShotsUseCase]
 *     - [ObserveAvailableClubsUseCase]
 *
 * The domain layer's use cases only depend on the [PlayerRepository] /
 * [ShotRepository] interfaces, keeping the domain module free of Android
 * or data-layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /** Bind the data-layer impl to the domain interface for player operations. */
    @Binds @Singleton
    abstract fun bindPlayerRepository(impl: PlayerRepositoryImpl): PlayerRepository

    /** Bind the data-layer impl to the domain interface for shot operations. */
    @Binds @Singleton
    abstract fun bindShotRepository(impl: ShotRepositoryImpl): ShotRepository
}
