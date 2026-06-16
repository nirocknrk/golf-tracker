package com.rapsodo.golftracker.feature.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.components.PlayerListItem
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.domain.model.Handedness
import com.rapsodo.golftracker.domain.model.Player

// ── Shared preview data ────────────────────────────────────────────────────────

private val previewPlayers = listOf(
    Player("p01", "James Anderson",   "JA", null, "club01", "Pine Haven",  "PH", Handedness.RIGHT,  5.2, 287.4, 218.3, 12.1, 2840, 1.48, 301.2, 231.5, 142),
    Player("p02", "Sarah Mitchell",   "SM", null, "club01", "Pine Haven",  "PH", Handedness.RIGHT, 12.8, 261.9, 198.6, 11.8, 3120, 1.43, 278.4, 210.2,  98),
    Player("p03", "Michael Chen",     "MC", null, "club01", "Pine Haven",  "PH", Handedness.LEFT,   8.4, 275.2, 208.4, 12.4, 2960, 1.46, 291.8, 224.7, 127),
    Player("p04", "David Wilson",     "DW", null, "club02", "Oakridge GC", "OG", Handedness.RIGHT,  3.1, 296.8, 225.4, 12.8, 2720, 1.51, 315.3, 238.6, 215),
    Player("p05", "Lisa Garcia",      "LG", null, "club02", "Oakridge GC", "OG", Handedness.RIGHT, 18.7, 239.4, 181.2, 10.9, 3380, 1.38, 254.7, 189.4,  51),
)

private val previewClubs = listOf("club01" to "PH", "club02" to "OG")

// ── Stateless screen shell (used by preview only) ─────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayersListScreenPreviewContent(
    searchQuery: String = "",
    selectedClubId: String? = null,
    isDark: Boolean = false,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title          = { Text("Players") },
                scrollBehavior = scrollBehavior,
                actions        = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.DarkMode, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Search bar
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query            = searchQuery,
                        onQueryChange    = {},
                        onSearch         = {},
                        expanded         = false,
                        onExpandedChange = {},
                        placeholder      = { Text("Search players") },
                        leadingIcon      = { Icon(Icons.Default.Search, contentDescription = null) },
                    )
                },
                expanded         = false,
                onExpandedChange = {},
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp),
            ) {}

            // Club filter chips
            LazyRow(
                modifier              = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(bottom = 8.dp),
            ) {
                item {
                    FilterChip(selected = selectedClubId == null, onClick = {}, label = { Text("All clubs") })
                }
                items(previewClubs.size) { i ->
                    val (id, short) = previewClubs[i]
                    FilterChip(selected = selectedClubId == id, onClick = {}, label = { Text(short) })
                }
            }

            // Player list
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(previewPlayers.size) { i ->
                    PlayerListItem(player = previewPlayers[i], onClick = {})
                    if (i < previewPlayers.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color    = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Players List (Light)", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlayersListLight() {
    GolfTheme(darkTheme = false) { PlayersListScreenPreviewContent() }
}

@Preview(name = "Players List (Dark)", showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPlayersListDark() {
    GolfTheme(darkTheme = true) { PlayersListScreenPreviewContent(isDark = true) }
}

@Preview(name = "Players List – club filter active", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlayersListClubFiltered() {
    GolfTheme(darkTheme = false) { PlayersListScreenPreviewContent(selectedClubId = "club01") }
}

@Preview(name = "Players List – search active", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlayersListSearchActive() {
    GolfTheme(darkTheme = false) { PlayersListScreenPreviewContent(searchQuery = "James") }
}
