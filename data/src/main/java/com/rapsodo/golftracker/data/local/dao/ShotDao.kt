package com.rapsodo.golftracker.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rapsodo.golftracker.data.local.entity.ShotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotDao {

    // ── Write ──────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(shots: List<ShotEntity>)

    @Query("DELETE FROM shots WHERE playerId = :playerId")
    suspend fun deleteByPlayer(playerId: String)

    // ── Read — Paging ──────────────────────────────────────────────────────

    /** All shots for [playerId], newest first. */
    @Query("SELECT * FROM shots WHERE playerId = :playerId ORDER BY seq DESC")
    fun pagingSource(playerId: String): PagingSource<Int, ShotEntity>

    /** Shots filtered by equipment label, newest first. */
    @Query(
        "SELECT * FROM shots WHERE playerId = :playerId AND equipment = :equipment ORDER BY seq DESC"
    )
    fun pagingSourceByClub(playerId: String, equipment: String): PagingSource<Int, ShotEntity>

    // ── Read — Flows ───────────────────────────────────────────────────────

    /** All shots for stats computation — ordered by seq ASC for trend charts. */
    @Query("SELECT * FROM shots WHERE playerId = :playerId ORDER BY seq ASC")
    fun observeAll(playerId: String): Flow<List<ShotEntity>>

    @Query(
        "SELECT * FROM shots WHERE playerId = :playerId ORDER BY seq DESC LIMIT :limit"
    )
    fun observeRecent(playerId: String, limit: Int): Flow<List<ShotEntity>>

    /** Distinct club names — sorted alphabetically for consistent chip order. */
    @Query(
        "SELECT DISTINCT equipment FROM shots WHERE playerId = :playerId ORDER BY equipment ASC"
    )
    fun observeAvailableClubs(playerId: String): Flow<List<String>>

    @Query("SELECT MIN(updatedAt) FROM shots WHERE playerId = :playerId")
    suspend fun oldestUpdatedAt(playerId: String): Long?
}
