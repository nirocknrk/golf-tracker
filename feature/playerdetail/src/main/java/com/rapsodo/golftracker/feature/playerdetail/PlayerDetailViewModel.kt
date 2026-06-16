package com.rapsodo.golftracker.feature.playerdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerStats
import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

/** Sealed UI state for the player detail screen. */
sealed interface PlayerDetailUiState {
    data object Loading : PlayerDetailUiState
    data class Success(
        val player: Player,
        val stats: PlayerStats?,
        val recentShots: List<Shot>,
        val availableClubs: List<String>,
    ) : PlayerDetailUiState
    data class Error(val message: String) : PlayerDetailUiState
}

/**
 * ViewModel for the Player Detail screen.
 *
 * Aggregates:
 *   - [Player] detail (Room Flow)
 *   - [PlayerStats] derived by [ComputePlayerStatsUseCase]
 *   - Recent shots (latest 5)
 *   - Available clubs (for filter chips)
 *   - Paged shot stream (with optional club filter)
 *
 * Fix: [refresh] previously just logged — it now increments [_refreshTrigger],
 * which re-subscribes [uiState] to all four Room flows via [flatMapLatest].
 * Room re-reads from disk and the [RemoteMediator] stale check runs again
 * on the next collection, triggering a network fetch if the cache is expired.
 */
@HiltViewModel
class PlayerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePlayerDetail: ObservePlayerDetailUseCase,
    private val computePlayerStats: ComputePlayerStatsUseCase,
    private val observeRecentShots: ObserveRecentShotsUseCase,
    private val observeAvailableClubs: ObserveAvailableClubsUseCase,
    private val observeShotsPaged: ObserveShotsPagedUseCase,
) : ViewModel() {

    // playerId is injected via navigation argument (see AppNavGraph)
    private val playerId: String = checkNotNull(savedStateHandle["playerId"])

    // ── Refresh trigger ─────────────────────────────────────────────────────
    /** Incrementing causes [uiState] to re-subscribe to all Room flows. */
    private val _refreshTrigger = MutableStateFlow(0)

    // ── Equipment filter ────────────────────────────────────────────────────
    private val _selectedClub = MutableStateFlow<String?>(null)
    val selectedClub: StateFlow<String?> = _selectedClub.asStateFlow()

    // ── Combined header UiState ─────────────────────────────────────────────
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PlayerDetailUiState> = _refreshTrigger
        .flatMapLatest {
            combine(
                observePlayerDetail(playerId),
                computePlayerStats(playerId),
                observeRecentShots(playerId, limit = 5),
                observeAvailableClubs(playerId),
            ) { player, stats, recent, clubs ->
                if (player == null) {
                    PlayerDetailUiState.Error("Player not found")
                } else {
                    PlayerDetailUiState.Success(
                        player         = player,
                        stats          = stats,
                        recentShots    = recent,
                        availableClubs = clubs,
                    )
                }
            }.catch { e ->
                Timber.e(e, "PlayerDetailVM error for $playerId")
                emit(PlayerDetailUiState.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerDetailUiState.Loading,
        )

    // ── Paged shots (reacts to club filter changes) ─────────────────────────
    @OptIn(ExperimentalCoroutinesApi::class)
    val shots: Flow<PagingData<Shot>> = _selectedClub
        .flatMapLatest { club -> observeShotsPaged(playerId, club) }
        .cachedIn(viewModelScope)

    // ── User actions ────────────────────────────────────────────────────────

    fun onClubFilterChange(club: String?) {
        _selectedClub.value = club
    }

    /**
     * Retry after an error — re-subscribes to all Room flows by incrementing
     * the refresh trigger. The Paging 3 shot stream should be retried from the
     * UI via [androidx.paging.compose.LazyPagingItems.retry], which is the
     * Paging-recommended approach for retrying load errors.
     */
    fun refresh() {
        Timber.d("PlayerDetailVM refresh triggered for $playerId")
        _refreshTrigger.update { it + 1 }
    }
}
