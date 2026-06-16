package com.rapsodo.golftracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity mirroring [com.rapsodo.golftracker.domain.model.Player].
 *
 * Stored once per player; updated on every RemoteMediator fetch.
 * [updatedAt] is used by RemoteMediator to detect stale cache (> 30 min).
 */
@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val initials: String,
    val avatarUrl: String?,
    val clubId: String,
    val clubName: String,
    val clubShort: String,
    val handed: String,             // stored as "RIGHT" / "LEFT"
    val handicap: Double,
    val avgBallSpeed: Double,
    val avgCarryDistance: Double,
    val avgLaunchAngle: Double,
    val avgSpinRate: Int,
    val avgSmashFactor: Double,
    val topSpeed: Double,
    val longestDrive: Double,
    val totalShots: Int,
    val updatedAt: Long = System.currentTimeMillis(),
)
