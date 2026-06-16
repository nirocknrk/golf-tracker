package com.rapsodo.golftracker.feature.shots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.components.ShotCard
import com.rapsodo.golftracker.core.ui.components.EmptyState
import com.rapsodo.golftracker.core.ui.components.ErrorMessage
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.domain.model.Shot

// ── Preview data ───────────────────────────────────────────────────────────────

private val previewEquipment = listOf("Driver", "7 Iron", "PW")

private val previewShots = (1..8).map { i ->
    Shot(
        id            = "shot_$i",
        playerId      = "p01",
        seq           = i,
        equipment     = previewEquipment[(i - 1) % previewEquipment.size],
        ballSpeed     = 260.0 + i * 3.5,
        launchAngle   = 10.5 + i * 0.2,
        carryDistance = 195.0 + i * 4,
        totalDistance = 210.0 + i * 4,
        spinRate      = 3100 - i * 30,
        smashFactor   = 1.42 + i * 0.01,
        offline       = if (i % 2 == 0) i * 1.1 else -i * 0.7,
        apex          = 28.0 + i,
        clubHeadSpeed = 183.0,
    )
}

// ── Stateless screen shell ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShotListScreenPreviewContent(
    selectedClub: String? = null,
    showEmpty: Boolean = false,
    showError: Boolean = false,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("Shot History") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        when {
            showError -> ErrorMessage(message = "Failed to load shots", onRetry = {},
                modifier = Modifier.padding(padding))
            showEmpty -> EmptyState(message = "No shots found",
                modifier = Modifier.padding(padding))
            else -> LazyColumn(
                contentPadding      = PaddingValues(
                    start  = 16.dp, end = 16.dp,
                    top    = padding.calculateTopPadding() + 4.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Filter chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedClub == null,
                                onClick  = {},
                                label    = { Text("All") },
                            )
                        }
                        items(previewEquipment.size) { i ->
                            FilterChip(
                                selected = selectedClub == previewEquipment[i],
                                onClick  = {},
                                label    = { Text(previewEquipment[i]) },
                            )
                        }
                    }
                }

                // Shot cards
                val visibleShots = if (selectedClub == null) previewShots
                                   else previewShots.filter { it.equipment == selectedClub }
                items(visibleShots.size) { i -> ShotCard(shot = visibleShots[i]) }

                // Append loading indicator
                item {
                    Box(Modifier.fillMaxWidth().padding(8.dp), Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "Shot List (Light)", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewShotListLight() {
    GolfTheme(darkTheme = false) { ShotListScreenPreviewContent() }
}

@Preview(name = "Shot List (Dark)", showBackground = true, showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShotListDark() {
    GolfTheme(darkTheme = true) { ShotListScreenPreviewContent() }
}

@Preview(name = "Shot List – Driver filter", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewShotListFiltered() {
    GolfTheme(darkTheme = false) { ShotListScreenPreviewContent(selectedClub = "Driver") }
}

@Preview(name = "Shot List – empty state", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewShotListEmpty() {
    GolfTheme(darkTheme = false) { ShotListScreenPreviewContent(showEmpty = true) }
}

@Preview(name = "Shot List – error state", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewShotListError() {
    GolfTheme(darkTheme = false) { ShotListScreenPreviewContent(showError = true) }
}
