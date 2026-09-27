package com.paraskcd.influentiallauncher.presentation

import android.content.Intent
import android.view.ViewTreeObserver
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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.paraskcd.influentiallauncher.infrastructure.NotificationShade
import com.paraskcd.influentiallauncher.infrastructure.SpotlightSearchLauncher
import com.paraskcd.influentiallauncher.presentation.model.DesktopAction
import com.paraskcd.influentiallauncher.presentation.utils.DesktopMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.StartMenuHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.rememberAboveTaskbarOffset
import com.paraskcd.influentiallauncher.windowing.infrastructure.DialogWindowSetup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun Desktop(activity: ComponentActivity) {
    var startOpen by rememberSaveable { mutableStateOf(false) }
    var hiddenFor by remember { mutableStateOf<DesktopAction?>(null) }
    var left by remember { mutableStateOf(false) }
    var direction by remember { mutableFloatStateOf(-1f) }
    val hidden = hiddenFor != null
    val aboveTaskbar = rememberAboveTaskbarOffset()
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val swipeDistance = with(density) { DesktopMetrics.searchSwipe.toPx() }
    val clockLift = with(density) { DesktopMetrics.searchClockLift.toPx() }
    val fade = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val settle = { target: Float -> scope.launch { fade.animateTo(target, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos)) } }
    val reveal = {
        hiddenFor = null
        left = false
        settle(1f)
    }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> { startOpen = false }
        activity.addOnNewIntentListener(listener)
        val observer = LifecycleEventObserver { _, event ->
            if (hiddenFor != DesktopAction.Search && hiddenFor != DesktopAction.App) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_PAUSE -> left = true
                Lifecycle.Event.ON_RESUME -> if (left) reveal()
                else -> Unit
            }
        }
        val focus = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hiddenFor != DesktopAction.Notifications) return@OnWindowFocusChangeListener
            if (!hasFocus) left = true else if (left) reveal()
        }
        activity.lifecycle.addObserver(observer)
        activity.window.decorView.viewTreeObserver.addOnWindowFocusChangeListener(focus)
        onDispose {
            activity.removeOnNewIntentListener(listener)
            activity.lifecycle.removeObserver(observer)
            activity.window.decorView.viewTreeObserver.removeOnWindowFocusChangeListener(focus)
        }
    }
    LaunchedEffect(hiddenFor) {
        val action = hiddenFor ?: return@LaunchedEffect
        fade.animateTo(0f, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos))
        val opened = when (action) {
            DesktopAction.Search -> SpotlightSearchLauncher.open(activity)
            DesktopAction.Notifications -> NotificationShade.expand(activity)
            DesktopAction.App -> true
        }
        if (!opened) {
            reveal()
            return@LaunchedEffect
        }
        delay(DesktopMetrics.leaveTimeoutMs)
        if (!left) reveal()
    }
    BackHandler(enabled = startOpen) { startOpen = false }
    val appLaunched = {
        startOpen = false
        hiddenFor = DesktopAction.App
    }
    val systemBarShown = startOpen || fade.value <= DesktopMetrics.searchStatusBarAlpha
    LaunchedEffect(systemBarShown) { DialogWindowSetup.setStatusBar(activity.window, visible = systemBarShown) }
    val taskbarEdge = screenWidth * (1f - TaskbarLayout.widthFraction) / 2f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { startOpen = false } }
            .pointerInput(startOpen, hidden) {
                if (startOpen || hidden) return@pointerInput
                var dragged = 0f
                detectVerticalDragGestures(
                    onDragStart = { dragged = 0f },
                    onDragEnd = {
                        if (abs(dragged) / swipeDistance >= DesktopMetrics.searchCommit) {
                            hiddenFor = if (dragged < 0f) DesktopAction.Search else DesktopAction.Notifications
                        } else {
                            settle(1f)
                        }
                    },
                    onDragCancel = { settle(1f) },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        dragged += amount
                        if (dragged != 0f) direction = sign(dragged)
                        val progress = (abs(dragged) / swipeDistance).coerceIn(0f, 1f)
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
                    translationY = direction * (1f - fade.value) * clockLift
                },
            sideInset = taskbarEdge
        )
    }
    TaskbarHost(
        startOpen = startOpen,
        onStartClick = { startOpen = !startOpen },
        onAppLaunched = appLaunched,
        visible = !hidden,
        alpha = fade.value
    )
    StatusBarHost(
        offsetX = taskbarEdge,
        offsetY = aboveTaskbar,
        visible = !startOpen && !hidden,
        alpha = fade.value
    )
    StartMenuHost(
        open = startOpen && !hidden,
        tabsOffsetY = aboveTaskbar,
        bottomOffset = aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap,
        onClose = { startOpen = false },
        onAppLaunched = appLaunched
    )
}
