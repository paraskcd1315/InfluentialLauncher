package com.paraskcd.influentiallauncher.presentation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.util.Consumer
import com.paraskcd.influentiallauncher.clock.presentation.ClockHeader
import com.paraskcd.influentiallauncher.startmenu.presentation.StartMenuHost
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.paraskcd.influentiallauncher.presentation.utils.DesktopMetrics
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.rememberAboveTaskbarOffset

@Composable
fun Desktop(activity: ComponentActivity) {
    var startOpen by rememberSaveable { mutableStateOf(false) }
    val aboveTaskbar = rememberAboveTaskbarOffset()
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> { startOpen = false }
        activity.addOnNewIntentListener(listener)
        onDispose { activity.removeOnNewIntentListener(listener) }
    }
    BackHandler(enabled = startOpen) { startOpen = false }
    val taskbarEdge = screenWidth * (1f - TaskbarLayout.widthFraction) / 2f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { startOpen = false } }
    ) {
        ClockHeader(modifier = Modifier.align(Alignment.TopStart), sideInset = taskbarEdge)
    }
    TaskbarHost(
        startOpen = startOpen,
        onStartClick = { startOpen = !startOpen },
        onAppLaunched = { startOpen = false }
    )
    StatusBarHost(
        offsetX = taskbarEdge,
        offsetY = aboveTaskbar,
        visible = !startOpen
    )
    StartMenuHost(
        open = startOpen,
        tabsOffsetY = aboveTaskbar,
        bottomOffset = aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap,
        onClose = { startOpen = false }
    )
}
