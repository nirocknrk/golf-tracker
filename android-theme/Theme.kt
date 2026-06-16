package com.example.golf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * GolfTheme — the Material 3 theme for Golf Performance Tracker.
 *
 * Usage (typically in your Activity / root composable):
 *
 *     GolfTheme(
 *         darkTheme = isSystemInDarkTheme(),   // "follow system" — the prototype default
 *         accent    = GolfAccent.Fairway,      // user-selectable accent
 *     ) {
 *         // your app
 *     }
 *
 * Persist the user's mode (System / Light / Dark) and accent in DataStore and feed
 * them in here — that reproduces the Tweaks panel from the prototype.
 *
 * Dynamic color (Material You) is intentionally OFF: this is a branded experience.
 */

enum class GolfAccent { Fairway, Ocean, Amber, Graphite }

// ── Fairway (default) ───────────────────────────────────────────────────
private val FairwayLight = lightColorScheme(
    primary = Color(0xFF2C6C46),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFAFF2C0),
    onPrimaryContainer = Color(0xFF00210F),
    inversePrimary = Color(0xFF94D5A5),
    secondary = Color(0xFF506352),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD3E8D3),
    onSecondaryContainer = Color(0xFF0E1F12),
    tertiary = Color(0xFF3A656E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBDEAF5),
    onTertiaryContainer = Color(0xFF001F25),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF6FBF3),
    onBackground = Color(0xFF181D18),
    surface = Color(0xFFF6FBF3),
    onSurface = Color(0xFF181D18),
    surfaceVariant = Color(0xFFDBE5DB),
    onSurfaceVariant = Color(0xFF414941),
    surfaceTint = Color(0xFF2C6C46),
    inverseSurface = Color(0xFF2D322C),
    inverseOnSurface = Color(0xFFEEF2EA),
    outline = Color(0xFF717971),
    outlineVariant = Color(0xFFC1C9BF),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFF6FBF3),
    surfaceDim = Color(0xFFD6DBD3),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0F5ED),
    surfaceContainer = Color(0xFFEAF0E8),
    surfaceContainerHigh = Color(0xFFE5EAE2),
    surfaceContainerHighest = Color(0xFFDFE4DC),
)

private val FairwayDark = darkColorScheme(
    primary = Color(0xFF94D5A5),
    onPrimary = Color(0xFF00391D),
    primaryContainer = Color(0xFF0F5130),
    onPrimaryContainer = Color(0xFFAFF2C0),
    inversePrimary = Color(0xFF2C6C46),
    secondary = Color(0xFFB7CCB7),
    onSecondary = Color(0xFF233426),
    secondaryContainer = Color(0xFF394B3B),
    onSecondaryContainer = Color(0xFFD3E8D3),
    tertiary = Color(0xFFA2CED9),
    onTertiary = Color(0xFF00363F),
    tertiaryContainer = Color(0xFF214D56),
    onTertiaryContainer = Color(0xFFBDEAF5),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF10140F),
    onBackground = Color(0xFFDFE4DC),
    surface = Color(0xFF10140F),
    onSurface = Color(0xFFDFE4DC),
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFC1C9BF),
    surfaceTint = Color(0xFF94D5A5),
    inverseSurface = Color(0xFFDFE4DC),
    inverseOnSurface = Color(0xFF2D322C),
    outline = Color(0xFF8B938A),
    outlineVariant = Color(0xFF414941),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF363A34),
    surfaceDim = Color(0xFF10140F),
    surfaceContainerLowest = Color(0xFF0B0F0A),
    surfaceContainerLow = Color(0xFF181D17),
    surfaceContainer = Color(0xFF1C211B),
    surfaceContainerHigh = Color(0xFF262B25),
    surfaceContainerHighest = Color(0xFF313630),
)

/**
 * Accents re-skin only the primary / secondary-container / tertiary roles on top of
 * the base scheme — surfaces stay the neutral green-tinted grays, exactly like the
 * prototype's Tweaks panel. Returned via ColorScheme.copy(...).
 */
private fun applyAccent(base: ColorScheme, dark: Boolean, accent: GolfAccent): ColorScheme =
    when (accent) {
        GolfAccent.Fairway -> base

        GolfAccent.Ocean -> if (dark) base.copy(
            primary = Color(0xFFADC6FF), onPrimary = Color(0xFF102F60),
            primaryContainer = Color(0xFF274777), onPrimaryContainer = Color(0xFFD8E2FF),
            secondaryContainer = Color(0xFF3B4858), onSecondaryContainer = Color(0xFFDAE2F9),
            tertiary = Color(0xFFC8BFFF), inversePrimary = Color(0xFF1B61C4),
            surfaceTint = Color(0xFFADC6FF),
        ) else base.copy(
            primary = Color(0xFF1B61C4), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD6E3FF), onPrimaryContainer = Color(0xFF001B3F),
            secondaryContainer = Color(0xFFDAE2F9), onSecondaryContainer = Color(0xFF131C2B),
            tertiary = Color(0xFF6B5CA5), inversePrimary = Color(0xFFADC6FF),
            surfaceTint = Color(0xFF1B61C4),
        )

        GolfAccent.Amber -> if (dark) base.copy(
            primary = Color(0xFFFFB77C), onPrimary = Color(0xFF4A2800),
            primaryContainer = Color(0xFF6C3A00), onPrimaryContainer = Color(0xFFFFDCC2),
            secondaryContainer = Color(0xFF534434), onSecondaryContainer = Color(0xFFF8DFC9),
            tertiary = Color(0xFFC2CA92), inversePrimary = Color(0xFFA85A08),
            surfaceTint = Color(0xFFFFB77C),
        ) else base.copy(
            primary = Color(0xFFA85A08), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDCC2), onPrimaryContainer = Color(0xFF2E1500),
            secondaryContainer = Color(0xFFF8DFC9), onSecondaryContainer = Color(0xFF2B1709),
            tertiary = Color(0xFF5B6236), inversePrimary = Color(0xFFFFB77C),
            surfaceTint = Color(0xFFA85A08),
        )

        GolfAccent.Graphite -> if (dark) base.copy(
            primary = Color(0xFFB4C9DA), onPrimary = Color(0xFF1E323F),
            primaryContainer = Color(0xFF344956), onPrimaryContainer = Color(0xFFCFE5FA),
            secondaryContainer = Color(0xFF3E4A52), onSecondaryContainer = Color(0xFFD9E2E9),
            tertiary = Color(0xFFCDC1E9), inversePrimary = Color(0xFF4C6173),
            surfaceTint = Color(0xFFB4C9DA),
        ) else base.copy(
            primary = Color(0xFF4C6173), onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFCFE5FA), onPrimaryContainer = Color(0xFF081E2A),
            secondaryContainer = Color(0xFFD9E2E9), onSecondaryContainer = Color(0xFF131C22),
            tertiary = Color(0xFF64597A), inversePrimary = Color(0xFFB4C9DA),
            surfaceTint = Color(0xFF4C6173),
        )
    }

@Composable
fun GolfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: GolfAccent = GolfAccent.Fairway,
    content: @Composable () -> Unit,
) {
    val base = if (darkTheme) FairwayDark else FairwayLight
    MaterialTheme(
        colorScheme = applyAccent(base, darkTheme, accent),
        typography = GolfTypography,
        shapes = GolfShapes,
        content = content,
    )
}
