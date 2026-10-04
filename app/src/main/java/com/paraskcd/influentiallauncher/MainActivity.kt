// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

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
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalPanelTint
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import com.paraskcd.influentiallauncher.presentation.viewmodels.DesktopViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.presentation.components.LocalIconLayers
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tiltSource: DeviceTiltSource

    @Inject
    lateinit var openApps: OpenApps

    @Inject
    lateinit var shellAccess: ShellAccess

    override fun onResume() {
        super.onResume()
        shellAccess.ensureReady()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (true) {
                    openApps.refresh()
                    delay(OpenAppsRefreshMs)
                }
            }
        }
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            val tilt = rememberDeviceTilt(this, tiltSource)
            val desktop: DesktopViewModel = hiltViewModel()
            val settings by desktop.settings.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalParallax provides tilt, LocalPanelTint provides settings.tintPanels) {
                InfluentialTheme {
                    val iconStyle by desktop.iconStyle.collectAsStateWithLifecycle()
                    val tint = InfTheme.colors.brandText.toArgb()
                    val iconBackground = InfTheme.colors.glassStrongBg.toArgb()
                    val iconLayers: suspend (AppId, Int) -> Pair<Bitmap, Bitmap>? = remember(tint, iconBackground, iconStyle) {
                        { id, px -> desktop.iconLayers(id, px, tint, iconBackground) }
                    }
                    CompositionLocalProvider(LocalIconLayers provides iconLayers) {
                        Desktop(activity = this, viewModel = desktop)
                    }
                }
            }
        }
    }

    private companion object {
        const val OpenAppsRefreshMs = 2_000L
    }
}
