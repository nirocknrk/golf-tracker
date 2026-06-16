package com.example.golf.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Golf Performance Tracker — color tokens.
 *
 * The full Material 3 ColorScheme role assignments live in Theme.kt. This file
 * exposes the raw brand seed, the four accent seeds, and the semantic colors you
 * need outside of MaterialTheme.colorScheme (e.g. charts), so nothing is hard-coded
 * at the call site.
 *
 * Source color (seed): Fairway green #2C6C46.
 * Generated with the Material 3 tonal-palette system; mirrors the HTML prototype.
 */

// ── Brand / accent seeds ────────────────────────────────────────────────
val SeedFairway  = Color(0xFF2C6C46)
val SeedOcean    = Color(0xFF1B61C4)
val SeedAmber    = Color(0xFFA85A08)
val SeedGraphite = Color(0xFF4C6173)

// ── Chart palette ───────────────────────────────────────────────────────
// Charts read these rather than colorScheme so they stay legible on both themes.
// Point/line colors are intentionally the theme primary & tertiary — pass
// MaterialTheme.colorScheme.primary / .tertiary in, or use these as fallbacks.
object GolfChart {
    const val PointAlpha = 0.55f          // dispersion scatter dot opacity
    const val TrendFillTopAlpha = 0.28f   // trend area gradient (top)
    const val GridLineAlpha = 0.5f        // gridline opacity over outlineVariant
}

// ── Profile placeholder background (matches ic_profile_placeholder.xml) ──
val ProfilePlaceholderBg = Color(0xFF5D7A66)
