package com.rapsodo.golftracker.data.mapper

import com.rapsodo.golftracker.data.local.entity.ShotEntity
import com.rapsodo.golftracker.data.remote.dto.ShotDto
import com.rapsodo.golftracker.domain.model.Shot

/**
 * Mapping functions for the Shot data pipeline:
 *
 *   Network (GolfApiService.getShots) → [ShotDto]
 *       ↓  [toEntity]
 *   Room (ShotDao.upsertAll) → [ShotEntity]
 *       ↓  [toDomain]
 *   Domain / UI → [Shot]
 *
 * Called by [ShotRemoteMediator] on every paged network response and by
 * [ShotRepositoryImpl] when exposing shots to the ViewModel via
 * [ObserveShotsPagedUseCase] / [ObserveRecentShotsUseCase].
 *
 * Note on name mismatches between the API and local schema:
 *   - `ShotDto.club`  → `ShotEntity.equipment`  (avoids SQL keyword clash)
 *   - `ShotDto.carry` → `ShotEntity.carryDistance`
 *   - `ShotDto.total` → `ShotEntity.totalDistance`
 * All other field names are 1:1.
 */

// ── DTO → Entity ───────────────────────────────────────────────────────────

/**
 * Converts a raw API shot object to a Room entity.
 *
 * Called inside [ShotRemoteMediator.load] in an atomic [withTransaction]
 * alongside [RemoteKeyEntity] writes.
 */
fun ShotDto.toEntity(): ShotEntity = ShotEntity(
    id             = id,
    playerId       = playerId,
    seq            = seq,
    equipment      = club,           // Renamed: "club" → "equipment" in Room
    ballSpeed      = ballSpeed,
    launchAngle    = launchAngle,
    carryDistance  = carry,          // Renamed: "carry" → "carryDistance"
    totalDistance  = total,          // Renamed: "total" → "totalDistance"
    spinRate       = spinRate,
    smashFactor    = smashFactor,
    offline        = offline,
    apex           = apex,
    clubHeadSpeed  = clubHeadSpeed,
)

// ── Entity → Domain ────────────────────────────────────────────────────────

/**
 * Converts a cached Room entity to a domain [Shot] model.
 *
 * All fields are 1:1 at this layer — the renaming was absorbed in [toEntity].
 * Called by the `PagingSource.map` transform in [ShotRepositoryImpl] and also
 * by [ObserveRecentShotsUseCase] for the most-recent 5 shots on the detail screen.
 */
fun ShotEntity.toDomain(): Shot = Shot(
    id             = id,
    playerId       = playerId,
    seq            = seq,
    equipment      = equipment,
    ballSpeed      = ballSpeed,
    launchAngle    = launchAngle,
    carryDistance  = carryDistance,
    totalDistance  = totalDistance,
    spinRate       = spinRate,
    smashFactor    = smashFactor,
    offline        = offline,
    apex           = apex,
    clubHeadSpeed  = clubHeadSpeed,
)

// ── List helpers ───────────────────────────────────────────────────────────

/** Batch-convert an API page to entities for a single [ShotDao.upsertAll] call. */
fun List<ShotDto>.toEntities() = map { it.toEntity() }

/** Batch-convert cached entities to domain models. */
fun List<ShotEntity>.toDomain() = map { it.toDomain() }
