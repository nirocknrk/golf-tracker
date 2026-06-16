package com.rapsodo.golftracker.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.domain.model.Shot

/**
 * Card displaying the key metrics for a single [Shot].
 *
 * Layout:
 *   Top row:  club badge | "#seq" label | offline indicator
 *   Metrics row: speed · carry · launch · spin
 */
@Composable
fun ShotCard(
    shot: Shot,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text     = shot.equipment.take(3).uppercase(),
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Text(
                    text  = "Shot #${shot.seq}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                val offlineSign = if (shot.offline < 0) "L" else "R"
                InfoPill(text = "${offlineSign} ${"%.1f".format(Math.abs(shot.offline))}m")
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ShotMetric(value = "%.1f".format(shot.ballSpeed),    unit = "km/h", label = "Speed")
                ShotMetric(value = "%.0f".format(shot.carryDistance), unit = "m",   label = "Carry")
                ShotMetric(value = "%.1f".format(shot.launchAngle),  unit = "°",    label = "Launch")
                ShotMetric(value = "%,d".format(shot.spinRate),       unit = "rpm",  label = "Spin")
            }
        }
    }
}

@Composable
fun ShotMetric(
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text  = value,
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(1.dp))
            Text(
                text     = unit,
                style    = MaterialTheme.typography.labelSmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewShot = Shot(
    id            = "shot_01",
    playerId      = "p01",
    seq           = 3,
    equipment     = "Driver",
    ballSpeed     = 287.4,
    launchAngle   = 12.1,
    carryDistance = 218.3,
    totalDistance = 231.5,
    spinRate      = 2840,
    smashFactor   = 1.48,
    offline       = -2.4,
    apex          = 31.2,
    clubHeadSpeed = 194.0,
)

@Preview(name = "ShotCard (Light)", showBackground = true)
@Composable
private fun PreviewShotCardLight() {
    GolfTheme(darkTheme = false) {
        Box(Modifier.padding(16.dp)) { ShotCard(shot = previewShot) }
    }
}

@Preview(name = "ShotCard (Dark)", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewShotCardDark() {
    GolfTheme(darkTheme = true) {
        Box(Modifier.padding(16.dp)) { ShotCard(shot = previewShot) }
    }
}
