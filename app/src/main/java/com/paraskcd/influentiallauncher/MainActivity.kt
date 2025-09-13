package com.paraskcd.influentiallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paraskcd.influentiallauncher.ui.dialogs.DockDialog
import com.paraskcd.influentiallauncher.ui.dialogs.WeatherMediaDialog
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.ui.screens.LauncherScreen
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
                LauncherScreen()
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (DockDialog.isShowing()) {
                return@addCallback
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DockDialog.ensureShown(this)
        WeatherMediaDialog.ensureShown(this)

    }
}