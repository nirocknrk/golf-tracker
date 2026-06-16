package com.rapsodo.golftracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.rapsodo.golftracker.core.ui.theme.GolfTheme
import com.rapsodo.golftracker.navigation.AppNavGraph
import com.rapsodo.golftracker.theme.ThemeMode
import com.rapsodo.golftracker.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVm: ThemeViewModel = hiltViewModel()
            val themeMode by themeVm.themeMode.collectAsStateWithLifecycle()
            val accent    by themeVm.accent.collectAsStateWithLifecycle()

            val isDark = when (themeMode) {
                ThemeMode.System -> isSystemInDarkTheme()
                ThemeMode.Light  -> false
                ThemeMode.Dark   -> true
            }

            GolfTheme(darkTheme = isDark, accent = accent) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    isDark        = isDark,
                    onToggleTheme = {
                        themeVm.setThemeMode(
                            if (themeMode == ThemeMode.Dark) ThemeMode.Light else ThemeMode.Dark
                        )
                    },
                )
            }
        }
    }
}
