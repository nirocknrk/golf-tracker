package com.example.golftracker.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * GolfShapes — the Material 3 shape scale for Golf Performance Tracker.
 *
 * Derived from the Claude Design prototype:
 *
 * extraSmall  (4dp)  — not used in current design
 * small       (8dp)  — FilterChip, InfoPill badge
 * medium      (12dp) — ShotCard club badge box, SummaryStat box
 * large       (16dp) — GolfCard, StatTile, ShotCard, FAB
 * extraLarge  (28dp) — SearchBar (height 56dp → half = 28dp → perfect stadium)
 */
val GolfShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
