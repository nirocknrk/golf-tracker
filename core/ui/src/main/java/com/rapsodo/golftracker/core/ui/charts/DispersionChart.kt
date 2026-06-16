package com.rapsodo.golftracker.core.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rapsodo.golftracker.core.ui.theme.GolfChart
import com.rapsodo.golftracker.core.ui.theme.GolfTheme

/**
 * Shot Dispersion Chart — scatter plot of lateral offline (X) vs carry distance (Y).
 *
 * Coordinate system:
 *   X-axis → offline (negative = left, positive = right)
 *   Y-axis → carry distance (bottom = 0, top = max carry)
 *
 * Draws:
 *   - Light grid lines at quartile intervals
 *   - Confidence ellipse sized by [dispersionRadius] around the cluster centroid
 *   - A scatter dot per shot, colored by laterality
 *   - Center-line (fairway) at x = 0
 *   - Axis labels (left / right)
 */
@Composable
fun DispersionChart(
    /** (offline m, carry m) pairs — offline < 0 = left, > 0 = right. */
    shots: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier,
    dispersionRadius: Double = 10.0,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    outlineVariant: Color = MaterialTheme.colorScheme.outlineVariant,
    onSurfaceVariant: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    if (shots.isEmpty()) return

    val measurer   = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 9.sp, color = onSurfaceVariant)

    val offlines  = shots.map { it.first.toFloat() }
    val carries   = shots.map { it.second.toFloat() }
    val minX      = (offlines.min() - 5f).coerceAtMost(-15f)
    val maxX      = (offlines.max() + 5f).coerceAtLeast(15f)
    val minY      = 0f
    val maxY      = carries.max() * 1.1f
    val centroidX = offlines.average().toFloat()
    val centroidY = carries.average().toFloat()

    Canvas(modifier = modifier.fillMaxSize()) {
        val pad    = 32.dp.toPx()
        val chartW = size.width  - pad * 2
        val chartH = size.height - pad * 2

        fun xToCanvas(x: Float) = pad + (x - minX) / (maxX - minX) * chartW
        fun yToCanvas(y: Float) = pad + chartH - (y - minY) / (maxY - minY) * chartH

        drawGridLines(outlineVariant, pad, chartW, chartH, minX, maxX, minY, maxY, ::xToCanvas, ::yToCanvas)

        // Center fairway line (x = 0)
        val cx0 = xToCanvas(0f)
        drawLine(
            color       = primaryColor.copy(alpha = 0.35f),
            start       = Offset(cx0, pad),
            end         = Offset(cx0, pad + chartH),
            strokeWidth = 1.5.dp.toPx(),
        )

        // Confidence ellipse
        val ellipseW = dispersionRadius.toFloat() / (maxX - minX) * chartW * 2
        val ellipseH = dispersionRadius.toFloat() / (maxY - minY) * chartH * 2
        val eCx      = xToCanvas(centroidX)
        val eCy      = yToCanvas(centroidY)
        drawOval(color = primaryColor.copy(alpha = 0.12f),
            topLeft = Offset(eCx - ellipseW / 2, eCy - ellipseH / 2), size = Size(ellipseW, ellipseH))
        drawOval(color = primaryColor.copy(alpha = 0.4f),
            topLeft = Offset(eCx - ellipseW / 2, eCy - ellipseH / 2), size = Size(ellipseW, ellipseH),
            style = Stroke(width = 1.dp.toPx()))

        // Scatter dots
        shots.forEach { (offline, carry) ->
            val dotX     = xToCanvas(offline.toFloat())
            val dotY     = yToCanvas(carry.toFloat())
            val dotColor = if (offline < 0) primaryColor.copy(alpha = GolfChart.PointAlpha)
                           else             primaryColor.copy(alpha = GolfChart.PointAlpha * 0.75f)
            drawCircle(color = dotColor, radius = 5.dp.toPx(), center = Offset(dotX, dotY))
            drawCircle(color = primaryColor.copy(alpha = 0.7f), radius = 5.dp.toPx(),
                center = Offset(dotX, dotY), style = Stroke(width = 0.8.dp.toPx()))
        }

        // Axis labels
        drawText(textMeasurer = measurer, text = "L",
            style = labelStyle, topLeft = Offset(pad - 12.dp.toPx(), pad + chartH / 2))
        drawText(textMeasurer = measurer, text = "R",
            style = labelStyle, topLeft = Offset(pad + chartW + 2.dp.toPx(), pad + chartH / 2))
    }
}

private fun DrawScope.drawGridLines(
    color: Color,
    pad: Float, chartW: Float, chartH: Float,
    minX: Float, maxX: Float, minY: Float, maxY: Float,
    xToCanvas: (Float) -> Float,
    yToCanvas: (Float) -> Float,
) {
    val alpha = GolfChart.GridLineAlpha.toFloat() * 0.5f
    listOf(0.25f, 0.5f, 0.75f).forEach { frac ->
        val cy = yToCanvas(minY + (maxY - minY) * frac)
        drawLine(color.copy(alpha = alpha), Offset(pad, cy), Offset(pad + chartW, cy), strokeWidth = 0.5.dp.toPx())
    }
    listOf(-15f, 0f, 15f).forEach { x ->
        if (x >= minX && x <= maxX) {
            val cx = xToCanvas(x)
            drawLine(color.copy(alpha = alpha), Offset(cx, pad), Offset(cx, pad + chartH), strokeWidth = 0.5.dp.toPx())
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewDispersionShots = listOf(
    -3.2 to 215.0, 1.5 to 222.0, -1.0 to 218.0, 4.2 to 210.0, -5.0 to 208.0,
    2.8  to 225.0, 0.5 to 219.0, -2.1 to 212.0,  3.3 to 220.0, -0.8 to 217.0,
)

@Preview(name = "DispersionChart (Light)", showBackground = true)
@Composable
private fun PreviewDispersionChartLight() {
    GolfTheme(darkTheme = false) {
        Box(Modifier.padding(8.dp)) {
            DispersionChart(shots = previewDispersionShots, dispersionRadius = 8.0,
                modifier = Modifier.fillMaxWidth().height(220.dp))
        }
    }
}

@Preview(name = "DispersionChart (Dark)", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDispersionChartDark() {
    GolfTheme(darkTheme = true) {
        Box(Modifier.padding(8.dp)) {
            DispersionChart(shots = previewDispersionShots, dispersionRadius = 8.0,
                modifier = Modifier.fillMaxWidth().height(220.dp))
        }
    }
}
