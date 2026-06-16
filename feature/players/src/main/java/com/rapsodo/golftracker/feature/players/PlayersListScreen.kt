package com.rapsodo.golftracker.feature.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rapsodo.golftracker.core.ui.components.*
import com.rapsodo.golftracker.domain.model.Player

/**
 * Players List — first screen in the nav graph.
 *
 * Features:
 *   - Dark / Light mode toggle button in the top-bar (opposite the "Players" title)
 *   - Horizontal club-type filter chips below the search bar
 *   - Searchable, Paging 3 player list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersListScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onPlayerClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: PlayersListViewModel = hiltViewModel(),
) {
    val scrollBehavior  = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val players         = vm.players.collectAsLazyPagingItems()
    val searchQuery     by vm.searchQuery.collectAsStateWithLifecycle()
    val availableClubs  by vm.availableClubs.collectAsStateWithLifecycle()
    val selectedClubId  by vm.selectedClubId.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar   = {
            LargeTopAppBar(
                title          = { Text("Players") },
                scrollBehavior = scrollBehavior,
                actions        = {
                    // ── Dark / Light toggle ─────────────────────────────
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector        = if (isDark) Icons.Default.LightMode
                                                else         Icons.Default.DarkMode,
                            contentDescription = if (isDark) "Switch to light mode"
                                                else         "Switch to dark mode",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // ── Search bar ─────────────────────────────────────────────────
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query            = searchQuery,
                        onQueryChange    = vm::onSearchQueryChange,
                        onSearch         = {},
                        expanded         = false,
                        onExpandedChange = {},
                        placeholder      = { Text("Search players") },
                        leadingIcon      = { Icon(Icons.Default.Search, contentDescription = null) },
                        // M3: trailing close icon appears only when there is active input
                        trailingIcon     = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = vm::clearSearch) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        } else null,
                    )
                },
                expanded         = false,
                onExpandedChange = {},
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp),
            ) {}

            // ── Club type filter chips ─────────────────────────────────────
            if (availableClubs.isNotEmpty()) {
                LazyRow(
                    modifier              = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding        = PaddingValues(bottom = 8.dp),
                ) {
                    // "All clubs" chip
                    item {
                        FilterChip(
                            selected = selectedClubId == null,
                            onClick  = { vm.onClubFilterChange(null) },
                            label    = { Text("All clubs") },
                        )
                    }
                    // One chip per distinct club
                    items(availableClubs) { (clubId, clubShort) ->
                        FilterChip(
                            selected = selectedClubId == clubId,
                            onClick  = {
                                vm.onClubFilterChange(
                                    if (selectedClubId == clubId) null else clubId
                                )
                            },
                            label = { Text(clubShort) },
                        )
                    }
                }
            }

            // ── Player list ────────────────────────────────────────────────
            PlayerPagingList(players = players, onPlayerClick = onPlayerClick)
        }
    }
}

@Composable
private fun PlayerPagingList(
    players: LazyPagingItems<Player>,
    onPlayerClick: (String) -> Unit,
) {
    when {
        players.loadState.refresh is LoadState.Loading && players.itemCount == 0 ->
            FullScreenLoading()

        players.loadState.refresh is LoadState.Error && players.itemCount == 0 -> {
            val e = (players.loadState.refresh as LoadState.Error).error
            ErrorMessage(
                message = e.localizedMessage ?: "Failed to load players",
                onRetry = { players.retry() },
            )
        }

        players.itemCount == 0 ->
            EmptyState(message = "No players found")

        else -> LazyColumn(
            contentPadding      = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(
                count = players.itemCount,
                key   = players.itemKey { it.id },
            ) { index ->
                val player = players[index]
                if (player != null) {
                    PlayerListItem(player = player, onClick = { onPlayerClick(player.id) })
                    if (index < players.itemCount - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color    = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                } else {
                    // Placeholder while the next page loads
                    ListItem(
                        headlineContent   = { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) },
                        supportingContent = {},
                    )
                }
            }

            // Append-loading spinner at the bottom of the list
            if (players.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator() }
                }
            }
        }
    }
}
