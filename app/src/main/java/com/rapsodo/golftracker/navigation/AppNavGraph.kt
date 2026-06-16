package com.rapsodo.golftracker.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rapsodo.golftracker.feature.playerdetail.PlayerDetailScreen
import com.rapsodo.golftracker.feature.players.PlayersListScreen
import com.rapsodo.golftracker.feature.shots.ShotListScreen
import kotlinx.serialization.Serializable
import androidx.navigation.toRoute

// ── Sealed route definitions ────────────────────────────────────────────────

//sealed class Screen(val route: String) {
//    data object PlayersList : Screen("players")
//    data object PlayerDetail : Screen("players/{playerId}") {
//        fun createRoute(playerId: String) = "players/$playerId"
//    }
//    data object ShotList : Screen("players/{playerId}/shots") {
//        fun createRoute(playerId: String) = "players/$playerId/shots"
//    }
//}
sealed class Screen {

    // 2. Define the Serializable routes as nested objects/data classes
    @Serializable
    object PlayersList : Screen()

    @Serializable
    data class PlayerDetail(val playerId: String) : Screen()

    @Serializable
    data class ShotList(val playerId: String) : Screen()
}

// 3. Helper to make navigation clean
fun NavHostController.navigateTo(screen: Screen) {
    this.navigate(screen)
}

/**
 * Root navigation graph.
 *
 * Transitions: slide in from right on forward, slide out to right on back.
 * [isDark] and [onToggleTheme] are forwarded to [PlayersListScreen] so the
 * top-bar theme toggle button can switch between Light and Dark modes.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.PlayersList,
        modifier         = modifier,
        enterTransition  = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition   = { slideOutHorizontally(targetOffsetX = { -it / 3 }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 3 }) },
        popExitTransition  = { slideOutHorizontally(targetOffsetX = { it }) },
    ) {
        // ── Players list ─────────────────────────────────────────────────
        composable<Screen.PlayersList>{
            PlayersListScreen(
                isDark        = isDark,
                onToggleTheme = onToggleTheme,
                onPlayerClick = { playerId ->
                    navController.navigateTo(Screen.PlayerDetail(playerId))
                },
            )
        }

        // ── Player detail ────────────────────────────────────────────────
        composable<Screen.PlayerDetail> {
            PlayerDetailScreen(
                onBack         = { navController.popBackStack() },
                onViewAllShots = { playerId ->
                    navController.navigateTo(Screen.ShotList(playerId))
                },
            )
        }

        // ── Shot list ────────────────────────────────────────────────────
        composable<Screen.ShotList> {
            ShotListScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
