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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rapsodo.golftracker.core.ui.theme.GolfChart
import com.rapsodo.golftracker.core.ui.theme.GolfTheme

/**
 * Ball Speed Trend Chart — area+line chart of ball speed across shot sequence.
 *
 * X-axis → shot sequence (left = oldest, right = newest)
 * Y-axis → ball speed in km/h
 *
 * Draws:
 *   - Gradient fill area under the line
 *   - Smooth polyline connecting data points
 *   - Dot at each data point
 *   - Subtle horizontal grid lines at min/avg/max
 */
@Composable
fun BallSpeedTrendChart(
    /** Ball speed values ordered by shot sequence (oldest to newest). */
    speeds: List<Double>,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    outlineVariant: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    if (speeds.size < 2) return

    val data   = speeds.map { it.toFloat() }
    val minVal = data.min() * 0.95f
    val maxVal = data.max() * 1.05f
    val range  = maxVal - minVal

    Canvas(modifier = modifier.fillMaxSize()) {
        val pad    = 16.dp.toPx()
        val chartW = size.width  - pad * 2
        val chartH = size.height - pad * 2

        fun xAt(i: Int)   = pad + i.toFloat() / (data.size - 1) * chartW
        fun yAt(v: Float) = pad + chartH - (v - minVal) / range * chartH

        val points = data.mapIndexed { i, v -> Offset(xAt(i), yAt(v)) }

        // Horizontal grid at min / avg / max
        drawGridLines(outlineVariant, pad, chartW, minVal, maxVal, data.average().toFloat(), ::yAt)

        // Area fill path
        val areaPath = Path().apply {
            moveTo(points.first().x, pad + chartH)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, pad + chartH)
            close()
        }
        drawPath(
            path  = areaPath,
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0f to primaryColor.copy(alpha = GolfChart.TrendFillTopAlpha),
                    1f to primaryColor.copy(alpha = 0.01f),
                ),
                startY = pad,
                endY   = pad + chartH,
            ),
        )

        // Trend line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawPath(path = linePath, color = primaryColor, style = Stroke(width = 2.dp.toPx()))

        // Data point dots
        points.forEach { p ->
            drawCircle(color = primaryColor, radius = 3.5.dp.toPx(), center = p)
            drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 1.8.dp.toPx(), center = p)
        }
    }
}

private fun DrawScope.drawGridLines(
    color: Color,
    pad: Float, chartW: Float,
    minVal: Float, maxVal: Float, avg: Float,
    yAt: (Float) -> Float,
) {
    listOf(minVal, avg, maxVal).forEach { v ->
        val y = yAt(v)
        drawLine(
            color       = color.copy(alpha = 0.4f),
            start       = Offset(pad, y),
            end         = Offset(pad + chartW, y),
            strokeWidth = 0.5.dp.toPx(),
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewSpeeds = listOf(281.0, 285.0, 279.0, 290.0, 287.0, 293.0, 289.0, 295.0, 291.0, 288.0)

@Preview(name = "BallSpeedTrendChart (Light)", showBackground = true)
@Composable
private fun PreviewBallSpeedTrendChartLight() {
    GolfTheme(darkTheme = false) {
        Box(Modifier.padding(8.dp)) {
            BallSpeedTrendChart(speeds = previewSpeeds,
                modifier = Modifier.fillMaxWidth().height(160.dp))
        }
    }
}

@Preview(name = "BallSpeedTrendChart (Dark)", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewBallSpeedTrendChartDark() {
    GolfTheme(darkTheme = true) {
        Box(Modifier.padding(8.dp)) {
            BallSpeedTrendChart(speeds = previewSpeeds,
                modifier = Modifier.fillMaxWidth().height(160.dp))
        }
    }
}
