package com.rapsodo.golftracker.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.domain.model.Handedness
import com.rapsodo.golftracker.domain.model.Player

/**
 * Single row in the players list.
 *
 * Layout:
 *   [Avatar] [Name + club short + HCP]  |  [Avg speed km/h]
 *                                           [Avg carry m]
 */
@Composable
fun PlayerListItem(
    player: Player,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.clickable(onClick = onClick),
        headlineContent = {
            Text(
                text     = player.name,
                style    = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoPill(text = player.clubShort)
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = "HCP ${player.handicap}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        leadingContent = {
            GolfAvatar(
                initials  = player.initials,
                avatarUrl = player.avatarUrl,
                size      = 48,
            )
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text  = "%.1f".format(player.avgBallSpeed),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text  = "km/h",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text  = "${"%.0f".format(player.avgCarryDistance)}m carry",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

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

@Preview(name = "PlayerListItem (Light)", showBackground = true)
@Composable
private fun PreviewPlayerListItemLight() {
    GolfTheme(darkTheme = false) { PlayerListItem(player = previewPlayer, onClick = {}) }
}

@Preview(name = "PlayerListItem (Dark)", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewPlayerListItemDark() {
    GolfTheme(darkTheme = true) { PlayerListItem(player = previewPlayer, onClick = {}) }
}
