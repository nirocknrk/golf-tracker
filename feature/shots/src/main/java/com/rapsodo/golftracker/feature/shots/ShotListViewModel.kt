package com.rapsodo.golftracker.feature.shots

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rapsodo.golftracker.domain.model.Shot
import com.rapsodo.golftracker.domain.usecase.ObserveAvailableClubsUseCase
import com.rapsodo.golftracker.domain.usecase.ObserveShotsPagedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the standalone Shots List screen.
 *
 * Receives [playerId] from the nav back-stack saved state.
 * Exposes a Paging 3 [shots] stream and [availableClubs] for the filter chip row.
 */
@HiltViewModel
class ShotListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeShotsPaged: ObserveShotsPagedUseCase,
    observeAvailableClubs: ObserveAvailableClubsUseCase,
) : ViewModel() {

    private val playerId: String = checkNotNull(savedStateHandle["playerId"])

    // ── Club filter ─────────────────────────────────────────────────────────
    private val _selectedClub = MutableStateFlow<String?>(null)
    val selectedClub: StateFlow<String?> = _selectedClub.asStateFlow()

    /** Available equipment labels for this player — drives filter chips. */
    val availableClubs: StateFlow<List<String>> = observeAvailableClubs(playerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Paged shots ─────────────────────────────────────────────────────────
    @OptIn(ExperimentalCoroutinesApi::class)
    val shots: Flow<PagingData<Shot>> = _selectedClub
        .flatMapLatest { club ->
            Timber.d("ShotListVM paging playerId=$playerId club=$club")
            observeShotsPaged(playerId, club)
        }
        .cachedIn(viewModelScope)

    fun onClubFilterChange(club: String?) {
        _selectedClub.value = club
    }
}
