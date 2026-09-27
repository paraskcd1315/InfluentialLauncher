package com.paraskcd.influentiallauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.paraskcd.influentiallauncher.designsystem.theme.InfluentialTheme
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceTiltSource
import com.paraskcd.influentiallauncher.presentation.Desktop
import com.paraskcd.influentiallauncher.presentation.rememberDeviceTilt
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import androidx.compose.runtime.CompositionLocalProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tiltSource: DeviceTiltSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            val tilt = rememberDeviceTilt(this, tiltSource)
            CompositionLocalProvider(LocalParallax provides tilt) {
                InfluentialTheme {
                    Desktop(activity = this)
                }
            }
        }
    }
}
