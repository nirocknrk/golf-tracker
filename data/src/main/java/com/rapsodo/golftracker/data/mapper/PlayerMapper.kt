package com.rapsodo.golftracker.data.mapper

import com.rapsodo.golftracker.data.local.entity.PlayerEntity
import com.rapsodo.golftracker.data.remote.dto.PlayerDto
import com.rapsodo.golftracker.domain.model.Handedness
import com.rapsodo.golftracker.domain.model.Player

/**
 * Mapping functions for the Player data pipeline:
 *
 *   Network (GolfApiService) → [PlayerDto]
 *       ↓  [toEntity]
 *   Room (GolfDatabase) → [PlayerEntity]
 *       ↓  [toDomain]
 *   Domain / UI → [Player]
 *
 * These are pure extension functions with no side-effects. They are called:
 *   - By [PlayerRemoteMediator] when writing a page of DTOs into Room
 *     (`response.data.toEntities()` inside a `db.withTransaction` block).
 *   - By [PlayerRepositoryImpl] (via `PagingSource.map`) when converting the
 *     cached [PlayerEntity] rows to [Player] domain objects for the ViewModel.
 *
 * Note on name mismatches between the API and the local schema:
 *   - `PlayerDto.avgCarry`  → `PlayerEntity.avgCarryDistance`
 *   - `PlayerDto.shotCount` → `PlayerEntity.totalShots`
 * All other field names are 1:1.
 */

// ── DTO → Entity (network → Room) ─────────────────────────────────────────

/**
 * Converts a raw API response object to a Room entity ready for upsert.
 *
 * Called inside [PlayerRemoteMediator.load] within an atomic [withTransaction]
 * so partial writes are never committed to the database.
 */
fun PlayerDto.toEntity(): PlayerEntity = PlayerEntity(
    id               = id,
    name             = name,
    initials         = initials,
    avatarUrl        = avatarUrl,
    clubId           = clubId,
    clubName         = clubName,
    clubShort        = clubShort,
    handed           = handed,
    handicap         = handicap,
    avgBallSpeed     = avgBallSpeed,
    avgCarryDistance = avgCarry,       // API field name differs from DB column
    avgLaunchAngle   = avgLaunchAngle,
    avgSpinRate      = avgSpinRate,
    avgSmashFactor   = avgSmashFactor,
    topSpeed         = topSpeed,
    longestDrive     = longestDrive,
    totalShots       = shotCount,      // API field name differs from DB column
)

// ── Entity → Domain (Room → UI) ────────────────────────────────────────────

/**
 * Converts a cached Room entity to a domain [Player] model.
 *
 * The [handed] field is stored as a plain String in Room (avoids a TypeConverter),
 * and resolved back to the [Handedness] enum here via [Handedness.fromString].
 *
 * Called by the [PagingSource] `.map { }` transform in [PlayerRepositoryImpl],
 * so every page of Room rows is converted to domain objects before reaching
 * the ViewModel.
 */
fun PlayerEntity.toDomain(): Player = Player(
    id               = id,
    name             = name,
    initials         = initials,
    avatarUrl        = avatarUrl,
    clubId           = clubId,
    clubName         = clubName,
    clubShort        = clubShort,
    handed           = Handedness.fromString(handed),
    handicap         = handicap,
    avgBallSpeed     = avgBallSpeed,
    avgCarryDistance = avgCarryDistance,
    avgLaunchAngle   = avgLaunchAngle,
    avgSpinRate      = avgSpinRate,
    avgSmashFactor   = avgSmashFactor,
    topSpeed         = topSpeed,
    longestDrive     = longestDrive,
    totalShots       = totalShots,
)

// ── List helpers ───────────────────────────────────────────────────────────

/** Batch-convert an entire API page to entities for a single Room upsert call. */
fun List<PlayerDto>.toEntities() = map { it.toEntity() }

/** Batch-convert a list of Room entities to domain models. */
fun List<PlayerEntity>.toDomain() = map { it.toDomain() }
