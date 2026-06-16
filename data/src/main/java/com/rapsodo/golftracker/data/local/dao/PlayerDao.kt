package com.rapsodo.golftracker.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rapsodo.golftracker.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

/** Lightweight Room projection for club filter chips. */
data class ClubProjection(val clubId: String, val clubShort: String)

@Dao
interface PlayerDao {

    // Write

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(players: List<PlayerEntity>)

    @Query("DELETE FROM players")
    suspend fun deleteAll()

    // Read - Paging

    @Query("SELECT * FROM players ORDER BY avgBallSpeed DESC")
    fun pagingSourceByBallSpeed(): PagingSource<Int, PlayerEntity>

    @Query("SELECT * FROM players ORDER BY avgCarryDistance DESC")
    fun pagingSourceByCarry(): PagingSource<Int, PlayerEntity>

    @Query("SELECT * FROM players ORDER BY name ASC")
    fun pagingSourceByName(): PagingSource<Int, PlayerEntity>

    @Query("SELECT * FROM players WHERE clubId = :clubId ORDER BY avgBallSpeed DESC")
    fun pagingSourceByClub(clubId: String): PagingSource<Int, PlayerEntity>

    @Query("SELECT * FROM players WHERE name LIKE '%' || :query || '%' ORDER BY avgBallSpeed DESC")
    fun pagingSourceByQuery(query: String): PagingSource<Int, PlayerEntity>

    /** Combined text search + club filter — used when both are active simultaneously. */
    @Query(
        "SELECT * FROM players WHERE name LIKE '%' || :query || '%' AND clubId = :clubId ORDER BY avgBallSpeed DESC"
    )
    fun pagingSourceByQueryAndClub(query: String, clubId: String): PagingSource<Int, PlayerEntity>

    // Read - Single

    @Query("SELECT * FROM players WHERE id = :id")
    fun observeById(id: String): Flow<PlayerEntity?>

    /** Oldest updatedAt in the table - used for cache-freshness checks. */
    @Query("SELECT MIN(updatedAt) FROM players")
    suspend fun oldestUpdatedAt(): Long?

    /** Distinct clubs present in the cache - used for filter chips. */
    @Query("SELECT DISTINCT clubId, clubShort FROM players ORDER BY clubShort ASC")
    fun observeAvailableClubs(): Flow<List<ClubProjection>>
}
