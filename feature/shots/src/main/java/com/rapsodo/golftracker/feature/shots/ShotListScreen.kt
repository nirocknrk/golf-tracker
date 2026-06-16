package com.rapsodo.golftracker.feature.shots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.rapsodo.golftracker.core.ui.components.*

/**
 * Standalone shots screen — full list of shots for one player.
 *
 * Navigated to from [PlayerDetailScreen] via "View all" button.
 * Equipment filter chips run above the list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShotListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ShotListViewModel = hiltViewModel(),
) {
    val scrollBehavior   = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val shots            = vm.shots.collectAsLazyPagingItems()
    val availableClubs   by vm.availableClubs.collectAsStateWithLifecycle()
    val selectedClub     by vm.selectedClub.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar   = {
            TopAppBar(
                title = { Text("Shot History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            contentPadding      = PaddingValues(
                start  = 16.dp, end = 16.dp,
                top    = innerPadding.calculateTopPadding() + 4.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Filter chip row
            if (availableClubs.isNotEmpty()) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedClub == null,
                                onClick  = { vm.onClubFilterChange(null) },
                                label    = { Text("All") },
                            )
                        }
                        items(availableClubs) { club ->
                            FilterChip(
                                selected = selectedClub == club,
                                onClick  = { vm.onClubFilterChange(if (selectedClub == club) null else club) },
                                label    = { Text(club) },
                            )
                        }
                    }
                }
            }

            // Load states
            when {
                shots.loadState.refresh is LoadState.Loading && shots.itemCount == 0 ->
                    item { FullScreenLoading(Modifier.fillParentMaxSize()) }

                shots.loadState.refresh is LoadState.Error && shots.itemCount == 0 ->
                    item {
                        val e = (shots.loadState.refresh as LoadState.Error).error
                        ErrorMessage(
                            message = e.localizedMessage ?: "Failed to load shots",
                            onRetry = { shots.retry() },
                        )
                    }

                shots.itemCount == 0 ->
                    item { EmptyState(message = "No shots found") }
            }

            // Shot cards
            items(
                count = shots.itemCount,
                key   = shots.itemKey { it.id },
            ) { i ->
                shots[i]?.let { shot -> ShotCard(shot = shot) }
            }

            if (shots.loadState.append is LoadState.Loading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(8.dp), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
