package com.rapsodo.golftracker.feature.playerdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.rapsodo.golftracker.core.ui.charts.BallSpeedTrendChart
import com.rapsodo.golftracker.core.ui.charts.DispersionChart
import com.rapsodo.golftracker.core.ui.components.*
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerStats
import com.rapsodo.golftracker.domain.model.Shot

/**
 * Player Detail screen.
 *
 * Sections (top to bottom in a scrollable LazyColumn):
 *   1. Hero header (avatar, name, club, handicap, info pills)
 *   2. Stats grid (6 tiles: speed, carry, launch, spin, smash, shots)
 *   3. Shot Dispersion chart
 *   4. Ball Speed Trend chart
 *   5. Recent Shots (last 5)
 *   6. Full shot log (Paging 3, filtered by club chip row)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    onBack: () -> Unit,
    onViewAllShots: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: PlayerDetailViewModel = hiltViewModel(),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val selectedClub by vm.selectedClub.collectAsStateWithLifecycle()
    val shots = vm.shots.collectAsLazyPagingItems()

    // Show FAB only when player data is loaded
    val playerIdForFab = (uiState as? PlayerDetailUiState.Success)?.player?.id

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar   = {
            LargeTopAppBar(
                title = {
                    if (uiState is PlayerDetailUiState.Success)
                        Text((uiState as PlayerDetailUiState.Success).player.name)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (playerIdForFab != null) {
                FloatingActionButton(
                    onClick           = { onViewAllShots(playerIdForFab) },
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    contentColor      = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.List,
                        contentDescription = "View all shots",
                    )
                }
            }
        },
    ) { innerPadding ->
        when (val state = uiState) {
            PlayerDetailUiState.Loading  -> FullScreenLoading(Modifier.padding(innerPadding))
            is PlayerDetailUiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = vm::refresh,
                modifier = Modifier.padding(innerPadding),
            )
            is PlayerDetailUiState.Success -> PlayerDetailContent(
                player        = state.player,
                stats         = state.stats,
                recentShots   = state.recentShots,
                availableClubs = state.availableClubs,
                selectedClub  = selectedClub,
                onClubFilter  = vm::onClubFilterChange,
                onViewAllShots = { onViewAllShots(state.player.id) },
                shots         = shots,
                innerPadding  = innerPadding,
            )
        }
    }
}

@Composable
private fun PlayerDetailContent(
    player: Player,
    stats: PlayerStats?,
    recentShots: List<Shot>,
    availableClubs: List<String>,
    selectedClub: String?,
    onClubFilter: (String?) -> Unit,
    onViewAllShots: () -> Unit,
    shots: androidx.paging.compose.LazyPagingItems<Shot>,
    innerPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding      = PaddingValues(
            start  = 16.dp, end = 16.dp,
            top    = innerPadding.calculateTopPadding() + 8.dp,
            bottom = innerPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Hero section ─────────────────────────────────────────────────
        item {
            PlayerHeroSection(player = player)
        }

        // ── Stats grid ───────────────────────────────────────────────────
        if (stats != null) {
            item { SectionLabel("Performance Stats") }
            item { StatsGrid(stats = stats) }
        }

        // ── Dispersion chart ─────────────────────────────────────────────
        if (recentShots.isNotEmpty()) {
            item { SectionLabel("Shot Dispersion") }
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    DispersionChart(
                        shots = recentShots.map { it.offline to it.carryDistance },
                        dispersionRadius = stats?.dispersionRadius ?: 10.0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(8.dp),
                    )
                }
            }

            // ── Ball speed trend ─────────────────────────────────────────
            item { SectionLabel("Ball Speed Trend") }
            item {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    BallSpeedTrendChart(
                        speeds = recentShots.sortedBy { it.seq }.map { it.ballSpeed },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(8.dp),
                    )
                }
            }

            // ── Recent shots ─────────────────────────────────────────────
            item { SectionLabel("Recent Shots") }
            items(recentShots) { shot ->
                ShotCard(shot = shot)
            }
        }

        // ── Club filter chips ────────────────────────────────────────────
        if (availableClubs.isNotEmpty()) {
            item { SectionLabel("All Shots") }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        FilterChip(
                            selected = selectedClub == null,
                            onClick  = { onClubFilter(null) },
                            label    = { Text("All") },
                        )
                    }
                    items(availableClubs) { club ->
                        FilterChip(
                            selected = selectedClub == club,
                            onClick  = { onClubFilter(if (selectedClub == club) null else club) },
                            label    = { Text(club) },
                        )
                    }
                }
            }
        }

        // ── Paged shots ──────────────────────────────────────────────────
        when {
            shots.loadState.refresh is LoadState.Loading && shots.itemCount == 0 -> {
                item { FullScreenLoading(Modifier.height(120.dp)) }
            }
            shots.loadState.refresh is LoadState.Error -> {
                item {
                    ErrorMessage(
                        message = "Failed to load shots",
                        onRetry = { shots.retry() },
                    )
                }
            }
        }

        items(
            count = shots.itemCount,
            key   = shots.itemKey { it.id },
        ) { i ->
            shots[i]?.let { shot -> ShotCard(shot = shot) }
        }

        if (shots.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
private fun PlayerHeroSection(player: Player) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GolfAvatar(initials = player.initials, avatarUrl = player.avatarUrl, size = 72)
        Spacer(Modifier.width(16.dp))
        Column {
            Text(player.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoPill(text = player.clubName)
                InfoPill(text = if (player.handicap >= 0) "+${player.handicap}" else "${player.handicap}")
                InfoPill(text = player.handed.name.take(1))
            }
        }
    }
}

@Composable
private fun StatsGrid(stats: PlayerStats) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatTile("%.1f".format(stats.avgBallSpeed), "km/h", "Avg Speed",    Modifier.weight(1f))
            StatTile("%.0f".format(stats.avgCarryDistance), "m",  "Avg Carry",    Modifier.weight(1f))
            StatTile("%.1f".format(stats.avgLaunchAngle), "°",  "Avg Launch",   Modifier.weight(1f))
        }
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatTile("%,d".format(stats.avgSpinRate),   "rpm", "Avg Spin",     Modifier.weight(1f))
            StatTile("%.2f".format(stats.avgSmashFactor), "",  "Smash Factor", Modifier.weight(1f))
            StatTile("${stats.totalShots}",             "",    "Total Shots",  Modifier.weight(1f))
        }
    }
}
