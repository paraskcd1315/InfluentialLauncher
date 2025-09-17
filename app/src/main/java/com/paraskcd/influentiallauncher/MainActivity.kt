package com.paraskcd.influentiallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.paraskcd.influentiallauncher.ui.dialogs.DockDialog
import com.paraskcd.influentiallauncher.ui.dialogs.WeatherMediaDialog
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.ui.screens.LauncherScreen
import com.paraskcd.influentiallauncher.ui.screens.ScreenManagerScreen
import com.paraskcd.influentiallauncher.viewmodels.LauncherStateViewModel
import com.paraskcd.influentiallauncher.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        super.onCreate(savedInstanceState)

        window.allowEnterTransitionOverlap = true
        window.allowReturnTransitionOverlap = true

        enableEdgeToEdge()
        setContent {
            val weatherViewModel: WeatherViewModel = hiltViewModel()
            DisposableEffect(Unit) {
                weatherViewModel.startPassiveLoop(isLauncherVisible = true)
                onDispose {
                    weatherViewModel.startPassiveLoop(isLauncherVisible = false)
                }
            }
            InfluentialLauncherTheme {
                val navController = rememberNavController()
                val launcherState: LauncherStateViewModel = hiltViewModel()
                NavHost(
                    navController = navController,
                    startDestination = "root"
                ) {
                    navigation(
                        startDestination = "home",
                        route = "root"
                    ) {
                        composable("home") {
                            LauncherScreen(
                                navController = navController,
                                launcherState = launcherState
                            )
                        }

                        composable("screen_manager") {
                            ScreenManagerScreen(
                                onBack = { navController.popBackStack() },
                                launcherState = launcherState
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DockDialog.ensureShown(this)
        WeatherMediaDialog.ensureShown(this)
    }
}