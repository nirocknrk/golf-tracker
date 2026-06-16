package com.rapsodo.golftracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for a single golf shot.
 *
 * Foreign key to [PlayerEntity] ensures shots are deleted when a player is removed.
 * Index on [playerId] + [seq] supports efficient paging and ordering queries.
 */
@Entity(
    tableName = "shots",
    foreignKeys = [
        ForeignKey(
            entity      = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns  = ["playerId"],
            onDelete      = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["playerId", "seq"]),
        Index(value = ["playerId", "equipment"]),
    ],
)
data class ShotEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val seq: Int,
    val equipment: String,
    val ballSpeed: Double,
    val launchAngle: Double,
    val carryDistance: Double,
    val totalDistance: Double,
    val spinRate: Int,
    val smashFactor: Double,
    val offline: Double,
    val apex: Double,
    val clubHeadSpeed: Double?,
    val updatedAt: Long = System.currentTimeMillis(),
)
