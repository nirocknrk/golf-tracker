package com.rapsodo.golftracker.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rapsodo.golftracker.core.ui.theme.GolfAccent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
private val KEY_ACCENT     = stringPreferencesKey("accent")

/** Persisted theme mode selection. */
enum class ThemeMode { System, Light, Dark }

/**
 * Reads and writes theme preferences from DataStore.
 *
 * Previously constructed DataStore directly from [ApplicationContext] via a
 * `Context.themeDataStore` extension property, which made this ViewModel
 * impossible to unit test (required a real Android Context).
 *
 * Fix: [DataStore<Preferences>] is now injected by Hilt from [DataStoreModule].
 * Tests can supply a [PreferenceDataStoreFactory.create]-based in-memory store.
 *
 * Consumed at [MainActivity] → [GolfTheme].
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val store: DataStore<Preferences>,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = store.data
        .map { prefs ->
            ThemeMode.entries.firstOrNull { it.name == prefs[KEY_THEME_MODE] }
                ?: ThemeMode.System
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.System)

    val accent: StateFlow<GolfAccent> = store.data
        .map { prefs ->
            GolfAccent.entries.firstOrNull { it.name == prefs[KEY_ACCENT] }
                ?: GolfAccent.Fairway
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GolfAccent.Fairway)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { store.edit { it[KEY_THEME_MODE] = mode.name } }
    }

    fun setAccent(accent: GolfAccent) {
        viewModelScope.launch { store.edit { it[KEY_ACCENT] = accent.name } }
    }
}
