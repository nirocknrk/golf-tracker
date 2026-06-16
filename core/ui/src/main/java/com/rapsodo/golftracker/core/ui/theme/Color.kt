package com.rapsodo.golftracker.core.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Golf Performance Tracker — color tokens.
 *
 * Brand seed: Fairway green #2C6C46.
 * Full Material 3 ColorScheme role assignments live in Theme.kt.
 * Chart constants are here so they stay legible on both themes.
 */

// ── Brand / accent seeds ────────────────────────────────────────────────
val SeedFairway  = Color(0xFF2C6C46)
val SeedOcean    = Color(0xFF1B61C4)
val SeedAmber    = Color(0xFFA85A08)
val SeedGraphite = Color(0xFF4C6173)

// ── Chart palette ───────────────────────────────────────────────────────
// Pass MaterialTheme.colorScheme.primary / .tertiary into charts; use these as fallbacks.
object GolfChart {
    const val PointAlpha       = 0.55f   // dispersion scatter dot opacity
    const val TrendFillTopAlpha = 0.28f  // trend area gradient top-stop
    const val GridLineAlpha    = 0.50f   // grid line opacity over outlineVariant
}

// ── Avatar placeholder background ───────────────────────────────────────
val ProfilePlaceholderBg = Color(0xFF5D7A66)
