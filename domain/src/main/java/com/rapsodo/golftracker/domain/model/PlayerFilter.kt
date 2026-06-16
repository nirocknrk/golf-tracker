package com.rapsodo.golftracker.domain.model

/**
 * Immutable filter + sort descriptor for the player list.
 *
 * Passed to [PlayerRepository.observePlayersPaged] and used as the cache-key
 * suffix in [RemoteKeyEntity.queryKey] to isolate per-filter pagination state.
 */
data class PlayerFilter(
    /** Optional free-text name search. Null = no filter. */
    val query: String? = null,
    /** Limit to players belonging to this club ID. Null = all clubs. */
    val clubId: String? = null,
    val sortBy: SortField = SortField.BALL_SPEED,
    val sortAscending: Boolean = false,
) {
    /**
     * Stable cache key for [RemoteKeyEntity.queryKey].
     * Must change whenever filter semantics change.
     */
    fun toCacheKey(): String =
        "players_${query}_${clubId}_${sortBy.name}_${if (sortAscending) "ASC" else "DESC"}"
}

/**
 * Supported sort fields. Each value must have a corresponding [PagingSource]
 * query in [PlayerDao] and a branch in [PlayerRepositoryImpl.pagingSourceFor].
 *
 * Previously-declared values (LAUNCH_ANGLE, SPIN_RATE, SMASH_FACTOR, HANDICAP)
 * were removed because they had no DAO queries and silently fell through to
 * BALL_SPEED ordering.
 */
enum class SortField { BALL_SPEED, CARRY_DISTANCE, NAME }
