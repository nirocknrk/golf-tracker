package com.rapsodo.golftracker.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * GolfShapes — Material 3 shape scale.
 *
 *   extraSmall  4dp  — (reserved / fine detail)
 *   small       8dp  — FilterChip, InfoPill badge
 *   medium     12dp  — ShotCard club badge, SummaryStat box
 *   large      16dp  — GolfCard, StatTile, FAB
 *   extraLarge 28dp  — SearchBar stadium shape (height 56dp ÷ 2)
 */
val GolfShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
