package com.rapsodo.golftracker.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.core.ui.theme.ProfilePlaceholderBg

// ── GolfAvatar ────────────────────────────────────────────────────────────────

/** Circular avatar: loads [avatarUrl] via Coil 3; falls back to initials on null/error. */
@Composable
fun GolfAvatar(
    initials: String,
    avatarUrl: String?,
    size: Int = 48,
    modifier: Modifier = Modifier,
) {
    val sizeDp = size.dp
    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(CircleShape)
            .background(ProfilePlaceholderBg),
        contentAlignment = Alignment.Center,
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model              = avatarUrl,
                contentDescription = null,
                modifier           = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text  = initials.take(2).uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
            )
        }
    }
}

// ── StatTile ──────────────────────────────────────────────────────────────────

/** Numeric stat tile (value + unit + label). Used in the 3-wide detail stats grid. */
@Composable
fun StatTile(
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier       = modifier,
        shape          = MaterialTheme.shapes.large,
        color          = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment     = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text  = value,
                    style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(2.dp))
                Text(
                    text     = unit,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── SectionLabel ──────────────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text     = text,
        style    = MaterialTheme.typography.titleSmall,
        color    = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

// ── InfoPill ──────────────────────────────────────────────────────────────────

@Composable
fun InfoPill(text: String, modifier: Modifier = Modifier) {
    Surface(
        shape    = MaterialTheme.shapes.small,
        color    = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier,
    ) {
        Text(
            text     = text,
            style    = MaterialTheme.typography.labelSmall,
            color    = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

// ── FullScreenLoading / EmptyState / ErrorMessage ─────────────────────────────

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(32.dp),
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(name = "GolfAvatar – initials (Light)", showBackground = true)
@Preview(name = "GolfAvatar – initials (Dark)", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewGolfAvatarInitials() {
    GolfTheme { Box(Modifier.padding(16.dp)) { GolfAvatar(initials = "JA", avatarUrl = null, size = 56) } }
}

@Preview(name = "StatTile (Light)", showBackground = true)
@Preview(name = "StatTile (Dark)", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewStatTile() {
    GolfTheme { Box(Modifier.padding(16.dp)) { StatTile(value = "287.4", unit = "km/h", label = "Avg Speed") } }
}

@Preview(name = "InfoPill", showBackground = true)
@Composable
private fun PreviewInfoPill() {
    GolfTheme { Box(Modifier.padding(16.dp)) { InfoPill(text = "Pine Haven") } }
}

@Preview(name = "EmptyState", showBackground = true, heightDp = 300)
@Composable
private fun PreviewEmptyState() {
    GolfTheme { EmptyState(message = "No players found") }
}

@Preview(name = "ErrorMessage", showBackground = true, heightDp = 200)
@Composable
private fun PreviewErrorMessage() {
    GolfTheme { ErrorMessage(message = "Failed to load data. Please try again.", onRetry = {}) }
}

@Preview(name = "FullScreenLoading", showBackground = true, heightDp = 200)
@Composable
private fun PreviewFullScreenLoading() {
    GolfTheme { FullScreenLoading() }
}
