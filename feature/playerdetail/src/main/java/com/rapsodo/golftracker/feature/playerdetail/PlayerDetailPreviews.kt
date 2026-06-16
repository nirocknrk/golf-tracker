package com.rapsodo.golftracker.feature.playerdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.charts.BallSpeedTrendChart
import com.rapsodo.golftracker.core.ui.charts.DispersionChart
import com.rapsodo.golftracker.core.ui.components.*
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.domain.model.Handedness
import com.rapsodo.golftracker.domain.model.Player
import com.rapsodo.golftracker.domain.model.PlayerStats
import com.rapsodo.golftracker.domain.model.Shot

// ── Preview data ───────────────────────────────────────────────────────────────

private val previewPlayer = Player(
    id               = "p01",
    name             = "James Anderson",
    initials         = "JA",
    avatarUrl        = null,
    clubId           = "club01",
    clubName         = "Pine Haven",
    clubShort        = "PH",
    handed           = Handedness.RIGHT,
    handicap         = 5.2,
    avgBallSpeed     = 287.4,
    avgCarryDistance = 218.3,
    avgLaunchAngle   = 12.1,
    avgSpinRate      = 2840,
    avgSmashFactor   = 1.48,
    topSpeed         = 301.2,
    longestDrive     = 231.5,
    totalShots       = 142,
)

private val previewStats = PlayerStats(
    avgBallSpeed     = 287.4,
    avgCarryDistance = 218.3,
    avgLaunchAngle   = 12.1,
    avgSpinRate      = 2840,
    avgSmashFactor   = 1.48,
    topSpeed         = 301.2,
    longestDrive     = 231.5,
    dispersionRadius = 8.0,
    ballSpeedSeries  = listOf(281.0, 285.0, 279.0, 290.0, 287.0, 293.0, 289.0, 295.0),
    totalShots       = 142,
)

private val previewShots = (1..5).map { i ->
    Shot(
        id            = "shot_$i",
        playerId      = "p01",
        seq           = i,
        equipment     = "Driver",
        ballSpeed     = 280.0 + i * 2,
        launchAngle   = 11.8 + i * 0.1,
        carryDistance = 210.0 + i * 3,
        totalDistance = 225.0 + i * 3,
        spinRate      = 2800 + i * 40,
        smashFactor   = 1.46,
        offline       = if (i % 2 == 0) i * 1.2 else -i * 0.8,
        apex          = 30.0 + i,
        clubHeadSpeed = 192.0,
    )
}

private val previewClubs = listOf("Driver", "7 Iron")

// ── Stateless screen shell ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerDetailScreenPreviewContent(
    selectedClub: String? = null,
    darkTheme: Boolean = false,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title          = { Text(previewPlayer.name) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = {},
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor   = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "View all shots")
            }
        },
    ) { padding ->
        LazyColumn(
            contentPadding      = PaddingValues(
                start  = 16.dp, end = 16.dp,
                top    = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 80.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    GolfAvatar(initials = previewPlayer.initials, avatarUrl = null, size = 72)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(previewPlayer.name, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            InfoPill(text = previewPlayer.clubName)
                            InfoPill(text = "+${previewPlayer.handicap}")
                            InfoPill(text = "R")
                        }
                    }
                }
            }

            // Stats grid
            item { SectionLabel("Performance Stats") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile("%.1f".format(previewStats.avgBallSpeed),     "km/h", "Avg Speed",    Modifier.weight(1f))
                        StatTile("%.0f".format(previewStats.avgCarryDistance), "m",    "Avg Carry",    Modifier.weight(1f))
                        StatTile("%.1f".format(previewStats.avgLaunchAngle),   "°",    "Avg Launch",   Modifier.weight(1f))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatTile("%,d".format(previewStats.avgSpinRate),        "rpm",  "Avg Spin",     Modifier.weight(1f))
                        StatTile("%.2f".format(previewStats.avgSmashFactor),   "",     "Smash Factor", Modifier.weight(1f))
                        StatTile("${previewStats.totalShots}",                  "",     "Total Shots",  Modifier.weight(1f))
                    }
                }
            }

            // Dispersion chart
            item { SectionLabel("Shot Dispersion") }
            item {
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    DispersionChart(
                        shots            = previewShots.map { it.offline to it.carryDistance },
                        dispersionRadius = previewStats.dispersionRadius,
                        modifier         = Modifier.fillMaxWidth().height(200.dp).padding(8.dp),
                    )
                }
            }

            // Ball speed trend
            item { SectionLabel("Ball Speed Trend") }
            item {
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    BallSpeedTrendChart(
                        speeds   = previewStats.ballSpeedSeries,
                        modifier = Modifier.fillMaxWidth().height(140.dp).padding(8.dp),
                    )
                }
            }

            // Recent shots
            item { SectionLabel("Recent Shots") }
            items(previewShots.size) { i -> ShotCard(shot = previewShots[i]) }

            // Club filter chips
            item { SectionLabel("All Shots") }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterChip(selected = selectedClub == null, onClick = {}, label = { Text("All") }) }
                    items(previewClubs.size) { i ->
                        FilterChip(
                            selected = selectedClub == previewClubs[i],
                            onClick  = {},
                            label    = { Text(previewClubs[i]) },
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Player Detail (Light)", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlayerDetailLight() {
    GolfTheme(darkTheme = false) { PlayerDetailScreenPreviewContent() }
}

@Preview(name = "Player Detail (Dark)", showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPlayerDetailDark() {
    GolfTheme(darkTheme = true) { PlayerDetailScreenPreviewContent(darkTheme = true) }
}

@Preview(name = "Player Detail – club filtered", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewPlayerDetailClubFiltered() {
    GolfTheme(darkTheme = false) { PlayerDetailScreenPreviewContent(selectedClub = "Driver") }
}
