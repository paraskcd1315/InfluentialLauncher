package com.paraskcd.influentiallauncher.presentation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.paraskcd.influentiallauncher.clock.presentation.ClockHeader
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.infrastructure.SpotlightSearchLauncher
import com.paraskcd.influentiallauncher.presentation.utils.DesktopMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.StartMenuHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.rememberAboveTaskbarOffset
import com.paraskcd.influentiallauncher.windowing.infrastructure.DialogWindowSetup
import kotlinx.coroutines.launch

@Composable
fun Desktop(activity: ComponentActivity) {
    var startOpen by rememberSaveable { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    var leftForSearch by remember { mutableStateOf(false) }
    val aboveTaskbar = rememberAboveTaskbarOffset()
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val swipeDistance = with(density) { DesktopMetrics.searchSwipe.toPx() }
    val clockLift = with(density) { DesktopMetrics.searchClockLift.toPx() }
    val fade = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val settle = { target: Float -> scope.launch { fade.animateTo(target, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos)) } }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> { startOpen = false }
        activity.addOnNewIntentListener(listener)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> if (searching) leftForSearch = true
                Lifecycle.Event.ON_RESUME -> if (leftForSearch) {
                    leftForSearch = false
                    searching = false
                    settle(1f)
                }
                else -> Unit
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose {
            activity.removeOnNewIntentListener(listener)
            activity.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(searching) {
        if (!searching) return@LaunchedEffect
        fade.animateTo(0f, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos))
        if (!SpotlightSearchLauncher.open(activity)) {
            searching = false
            settle(1f)
        }
    }
    BackHandler(enabled = startOpen) { startOpen = false }
    val systemBarShown = startOpen || fade.value <= DesktopMetrics.searchStatusBarAlpha
    LaunchedEffect(systemBarShown) { DialogWindowSetup.setStatusBar(activity.window, visible = systemBarShown) }
    val taskbarEdge = screenWidth * (1f - TaskbarLayout.widthFraction) / 2f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { startOpen = false } }
            .pointerInput(startOpen, searching) {
                if (startOpen || searching) return@pointerInput
                var dragged = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragged = 0f },
                    onDragEnd = {
                        if (-dragged / swipeDistance >= DesktopMetrics.searchCommit) searching = true else settle(1f)
                    },
                    onDragCancel = { settle(1f) },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        dragged = (dragged + amount).coerceAtMost(0f)
                        val progress = (-dragged / swipeDistance).coerceIn(0f, 1f)
                        scope.launch { fade.snapTo(1f - progress) }
                    }
                )
            }
    ) {
        ClockHeader(
            modifier = Modifier
                .align(Alignment.TopStart)
                .graphicsLayer {
                    alpha = fade.value
                    translationY = -(1f - fade.value) * clockLift
                },
            sideInset = taskbarEdge
        )
    }
    TaskbarHost(
        startOpen = startOpen,
        onStartClick = { startOpen = !startOpen },
        onAppLaunched = { startOpen = false },
        visible = !searching,
        alpha = fade.value
    )
    StatusBarHost(
        offsetX = taskbarEdge,
        offsetY = aboveTaskbar,
        visible = !startOpen && !searching,
        alpha = fade.value
    )
    StartMenuHost(
        open = startOpen && !searching,
        tabsOffsetY = aboveTaskbar,
        bottomOffset = aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap,
        onClose = { startOpen = false }
    )
}
