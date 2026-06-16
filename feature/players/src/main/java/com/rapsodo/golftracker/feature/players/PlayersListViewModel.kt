package com.rapsodo.golftracker.feature.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerFilter
import com.rapsodo.golftracker.domain.usecase.ObserveAvailablePlayerClubsUseCase
import com.rapsodo.golftracker.domain.usecase.ObservePlayersPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Players List screen.
 *
 * Exposes [players] as a Paging 3 [PagingData] stream cached in [viewModelScope].
 * [filter] is a hot [StateFlow] that drives the paged stream — any filter change
 * cancels the current Pager and starts a new one with the updated query key.
 *
 * Debounce strategy:
 *  - [onSearchQueryChange] uses a manual [Job]-based 300 ms debounce so that
 *    rapid keystrokes don't hammer the Pager with new queries.
 *  - [clearSearch] cancels any pending debounce and updates [_filter] immediately,
 *    so the ✕ button clears the list without the 300 ms lag.
 *
 *  The previous implementation put `.debounce(300ms)` on [_filter] itself, which
 *  meant even an explicit clear went through the delay — noticeably laggy UX.
 */
@HiltViewModel
class PlayersListViewModel @Inject constructor(
    private val observePlayersPaged: ObservePlayersPagedUseCase,
    private val observeAvailablePlayerClubs: ObserveAvailablePlayerClubsUseCase,
) : ViewModel() {

    // ── Filter state (drives the Pager) ────────────────────────────────────
    private val _filter = MutableStateFlow(PlayerFilter())
    val filter: StateFlow<PlayerFilter> = _filter.asStateFlow()

    // ── Raw search query (reflected to UI text field) ───────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Manual debounce job (replaces .debounce() on _filter) ──────────────
    private var searchDebounceJob: Job? = null

    // ── Available clubs for filter chips ───────────────────────────────────
    /** List of (clubId, clubShort) pairs derived from the cached player data. */
    val availableClubs: StateFlow<List<Pair<String, String>>> =
        observeAvailablePlayerClubs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Currently selected clubId (null = "All clubs"). */
    val selectedClubId: StateFlow<String?> = _filter
        .map { it.clubId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ── Paged players stream ────────────────────────────────────────────────
    // No .debounce() here — debounce is handled per-action in onSearchQueryChange.
    // This means club filter changes and clearSearch are always immediate.
    @OptIn(ExperimentalCoroutinesApi::class)
    val players: Flow<PagingData<Player>> = _filter
        .flatMapLatest { f ->
            Timber.d("PlayersListVM creating new Pager for filter=$f")
            observePlayersPaged(f)
        }
        .cachedIn(viewModelScope)

    // ── User actions ────────────────────────────────────────────────────────

    /**
     * Called on every keystroke in the search field.
     * Updates the visible text immediately, but waits 300 ms before committing
     * the query to [_filter] to avoid firing a new Pager on every character.
     */
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(300)
            _filter.update { it.copy(query = query.ifBlank { null }) }
        }
    }

    fun onClubFilterChange(clubId: String?) {
        _filter.update { it.copy(clubId = clubId) }
    }

    /**
     * Clears the search field and immediately resets the filter — no debounce.
     * Cancels any pending debounce job so an in-flight keystroke can't
     * accidentally re-apply a query after the clear.
     */
    fun clearSearch() {
        searchDebounceJob?.cancel()
        _searchQuery.value = ""
        _filter.update { it.copy(query = null) }
    }
}
