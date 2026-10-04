// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.presentation

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.layout.fillMaxWidth
import com.paraskcd.influentiallauncher.homescreen.presentation.HomeEditDone
import com.paraskcd.influentiallauncher.homescreen.presentation.HomeScreenHost
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.paraskcd.influentiallauncher.clock.presentation.ClockHeader
import com.paraskcd.influentiallauncher.controlcenter.presentation.ControlCenterHost
import com.paraskcd.influentiallauncher.glance.presentation.GlanceHost
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.infrastructure.NotificationShade
import com.paraskcd.influentiallauncher.infrastructure.SearchLauncher
import com.paraskcd.influentiallauncher.infrastructure.spotlight.SpotlightPeek
import com.paraskcd.influentiallauncher.presentation.utils.SearchPeekRelease
import com.paraskcd.influentiallauncher.infrastructure.WallpaperShift
import com.paraskcd.influentiallauncher.infrastructure.WallpaperZoom
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import kotlin.math.roundToInt
import com.paraskcd.influentiallauncher.presentation.model.DesktopAction
import com.paraskcd.influentiallauncher.presentation.utils.DesktopMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.StartMenuHost
import com.paraskcd.influentiallauncher.startmenu.presentation.StartSwipe
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.input.pointer.util.VelocityTracker
import com.paraskcd.influentiallauncher.statusbar.presentation.SearchPillHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarHost
import com.paraskcd.influentiallauncher.statusbar.presentation.StatusBarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarHost
import com.paraskcd.influentiallauncher.taskbar.presentation.TaskbarLayout
import com.paraskcd.influentiallauncher.taskbar.presentation.rememberAboveTaskbarOffset
import com.paraskcd.influentiallauncher.windowing.infrastructure.DialogWindowSetup
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Desktop(activity: ComponentActivity) {
    var startOpen by rememberSaveable { mutableStateOf(false) }
    var controlOpen by rememberSaveable { mutableStateOf(false) }
    var controlsReady by remember { mutableStateOf(false) }
    var homeOverview by remember { mutableStateOf(false) }
    var hiddenFor by remember { mutableStateOf<DesktopAction?>(null) }
    var left by remember { mutableStateOf(false) }
    var direction by remember { mutableFloatStateOf(-1f) }
    var introPending by remember { mutableStateOf(false) }
    var introPlaying by remember { mutableStateOf(false) }
    val searchAvailable = remember(activity) { mutableStateOf(SearchLauncher.available(activity)) }
    val hidden = hiddenFor != null
    val fade = remember { Animatable(1f) }
    val barsHidden = hidden && fade.value == 0f
    val aboveTaskbar = rememberAboveTaskbarOffset()
    val density = LocalDensity.current
    val screenWidth = with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
    val swipeDistance = with(density) { DesktopMetrics.searchSwipe.toPx() }
    val clockLift = with(density) { DesktopMetrics.searchClockLift.toPx() }
    val scope = rememberCoroutineScope()
    val settle = { target: Float -> scope.launch { fade.animateTo(target, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos)) } }
    val reveal = {
        hiddenFor = null
        left = false
        settle(1f)
    }
    val followSpotlight = { shown: Float ->
        if (hiddenFor == DesktopAction.Peek || hiddenFor == DesktopAction.Search) {
            scope.launch { fade.snapTo(1f - shown) }
        }
    }
    val peek = remember(activity) { SpotlightPeek(activity, onShown = { followSpotlight(it) }, onClosed = { reveal() }) }
    DisposableEffect(peek) {
        peek.bind()
        onDispose { peek.unbind() }
    }

    DisposableEffect(activity) {
        val keyguard = activity.getSystemService(KeyguardManager::class.java)
        val playIntro = {
            if (introPending) {
                introPending = false
                scope.launch {
                    fade.animateTo(1f, tween(DesktopMetrics.unlockIntroMs, easing = InfMotion.easeIos))
                    introPlaying = false
                }
            }
        }
        val listener = Consumer<Intent> {
            startOpen = false
            controlOpen = false
            if (hiddenFor == DesktopAction.Peek) reveal()
            playIntro()
        }
        activity.addOnNewIntentListener(listener)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && keyguard?.isKeyguardLocked != true) playIntro()
            if (event == Lifecycle.Event.ON_RESUME) {
                peek.bind()
                searchAvailable.value = SearchLauncher.available(activity)
            }
            if (event == Lifecycle.Event.ON_RESUME && hiddenFor == DesktopAction.Peek) reveal()
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
        val screen = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> if (hiddenFor == null) {
                        startOpen = false
                        controlOpen = false
                        direction = -1f
                        introPending = true
                        introPlaying = true
                        scope.launch { fade.snapTo(0f) }
                    }
                    Intent.ACTION_USER_PRESENT -> playIntro()
                    Intent.ACTION_SCREEN_ON -> if (keyguard?.isKeyguardLocked != true) playIntro()
                }
            }
        }
        activity.lifecycle.addObserver(observer)
        activity.window.decorView.viewTreeObserver.addOnWindowFocusChangeListener(focus)
        ContextCompat.registerReceiver(
            activity,
            screen,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose {
            activity.removeOnNewIntentListener(listener)
            activity.lifecycle.removeObserver(observer)
            activity.window.decorView.viewTreeObserver.removeOnWindowFocusChangeListener(focus)
            activity.unregisterReceiver(screen)
        }
    }
    LaunchedEffect(hiddenFor) {
        val action = hiddenFor ?: return@LaunchedEffect
        fade.animateTo(0f, tween(InfMotion.durMorphMs, easing = InfMotion.easeIos))
        if (action == DesktopAction.Peek) return@LaunchedEffect
        val opened = when (action) {
            DesktopAction.Search -> SearchLauncher.open(activity)
            DesktopAction.Notifications -> NotificationShade.expand(activity)
            DesktopAction.App, DesktopAction.Peek -> true
        }
        if (!opened) {
            reveal()
            return@LaunchedEffect
        }
        delay(DesktopMetrics.leaveTimeoutMs)
        if (!left) reveal()
    }
    BackHandler(enabled = startOpen) { startOpen = false }
    val startSwipe = remember { StartSwipe() }
    val startShown = startOpen || startSwipe.active
    val flingVelocity = with(density) { StartMenuMetrics.swipeFling.toPx() }
    val openStartBySwipe = remember(startSwipe, flingVelocity) {
        SwipeUp(
            onDrag = { up ->
                if (!startOpen) {
                    controlOpen = false
                    startSwipe.drag(up)
                }
            },
            onEnd = { velocity ->
                if (startSwipe.active) startOpen = startSwipe.releaseStaysOpen(velocity, flingVelocity)
            }
        )
    }
    val appLaunched = {
        startOpen = false
        controlOpen = false
        hiddenFor = DesktopAction.App
    }
    val systemBarShown = startShown || homeOverview || fade.value <= DesktopMetrics.searchStatusBarAlpha
    LaunchedEffect(systemBarShown) { DialogWindowSetup.setStatusBar(activity.window, visible = systemBarShown) }
    val landscape = isLandscape()
    var headerHeight by remember { mutableStateOf(0.dp) }
    val layoutDirection = LocalLayoutDirection.current
    val cutoutStart = with(density) { WindowInsets.displayCutout.getLeft(density, layoutDirection).toDp() }
    val statusTop = with(density) { WindowInsets.statusBarsIgnoringVisibility.getTop(density).toDp() }
    val navigationBottom = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    val taskbarEdge = if (landscape) maxOf(DesktopMetrics.landscapeInset, cutoutStart + DesktopMetrics.windowGap)
        else screenWidth * (1f - TaskbarLayout.widthFraction) / 2f
    val landscapeGridMargin = maxOf(statusTop, navigationBottom) + DesktopMetrics.windowGap
    val windowSize = LocalWindowInfo.current.containerSize
    var headerBounds by remember { mutableStateOf<Rect?>(null) }
    var gridBounds by remember { mutableStateOf<Rect?>(null) }
    val headerInk = rememberWallpaperInk(headerBounds, windowSize)
    val gridInk = rememberWallpaperInk(gridBounds, windowSize)
    val pillTop = statusTop + DesktopMetrics.windowGap
    val belowPill = pillTop + StatusBarLayout.height + DesktopMetrics.windowGap
    val introZoom = if (introPlaying) 1f - fade.value else 0f
    val chrome by animateFloatAsState(
        targetValue = if (homeOverview) 0f else 1f,
        animationSpec = tween(DesktopMetrics.overviewMs, easing = InfMotion.easeIos),
        label = "desktopChrome"
    )
    val chromeAlpha = fade.value * chrome
    val zoomTarget = maxOf(if (startShown || homeOverview) DesktopMetrics.startWallpaperZoom else 0f, introZoom) * DesktopMetrics.wallpaperZoomMax
    val wallpaperZoom = animateFloatAsState(
        targetValue = zoomTarget,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "wallpaperZoom"
    )
    val tilt = LocalParallax.current
    LaunchedEffect(Unit) {
        snapshotFlow {
            val lean = tilt.value.getDistance().coerceAtMost(1f)
            wallpaperZoom.value + lean * DesktopMetrics.tiltWallpaperZoom
        }.collect { WallpaperZoom.set(activity.window, it) }
    }
    val wallpaperDrift = with(density) { DesktopMetrics.wallpaperParallax.toPx() }
    val headerDrift = with(density) { DesktopMetrics.headerParallax.toPx() }
    val gridDrift = with(density) { DesktopMetrics.gridParallax.toPx() }
    LaunchedEffect(wallpaperDrift) {
        snapshotFlow { tilt.value }.collect {
            WallpaperShift.set(activity.window, (it.x * wallpaperDrift).roundToInt(), (it.y * wallpaperDrift).roundToInt())
        }
    }

    val barsHeight = rememberUpdatedState(
        with(density) {
            val bars = if (landscape) navigationBottom + DesktopMetrics.windowGap else aboveTaskbar
            (bars + StatusBarLayout.height + DesktopMetrics.windowGap).toPx()
        }
    )
    val barsWidth = rememberUpdatedState(if (landscape) with(density) { aboveTaskbar.toPx() } else 0f)
    val peekAllowed = rememberUpdatedState(!landscape)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    startOpen = false
                    controlOpen = false
                }
            }
            .pointerInput(startOpen, controlOpen, hidden, homeOverview) {
                if (startOpen || controlOpen || hidden || homeOverview) return@pointerInput
                var dragged = 0f
                var fromBars = false
                var peeking = false
                val tracker = VelocityTracker()
                detectVerticalDragGestures(
                    onDragStart = { start ->
                        dragged = 0f
                        peeking = false
                        tracker.resetTracking()
                        fromBars = start.y >= size.height - barsHeight.value || start.x >= size.width - barsWidth.value
                    },
                    onDragEnd = {
                        val velocityUp = -tracker.calculateVelocity().y
                        when {
                            fromBars -> openStartBySwipe.onEnd(velocityUp)
                            peeking -> if (SearchPeekRelease.opens(-dragged / swipeDistance, velocityUp, flingVelocity)) {
                                peek.commit(velocityUp)
                                hiddenFor = DesktopAction.Peek
                            } else {
                                peek.cancel(velocityUp)
                                settle(1f)
                            }
                            abs(dragged) / swipeDistance >= DesktopMetrics.searchCommit ->
                                hiddenFor = if (dragged < 0f) DesktopAction.Search else DesktopAction.Notifications
                            else -> settle(1f)
                        }
                    },
                    onDragCancel = {
                        when {
                            fromBars -> openStartBySwipe.onEnd(0f)
                            peeking -> {
                                peek.cancel(0f)
                                settle(1f)
                            }
                            else -> settle(1f)
                        }
                    },
                    onVerticalDrag = { change, amount ->
                        change.consume()
                        tracker.addPosition(change.uptimeMillis, change.position)
                        if (fromBars) {
                            openStartBySwipe.onDrag(-amount)
                            return@detectVerticalDragGestures
                        }
                        dragged += amount
                        if (dragged < 0f && !searchAvailable.value) {
                            dragged = 0f
                            return@detectVerticalDragGestures
                        }
                        if (!peeking && dragged < 0f && peekAllowed.value) peeking = peek.start()
                        if (peeking) peek.progress(-dragged / swipeDistance, swipeDistance)
                        if (dragged != 0f) direction = sign(dragged)
                        val progress = (abs(dragged) / swipeDistance).coerceIn(0f, 1f)
                        scope.launch { fade.snapTo(1f - progress) }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .then(if (landscape) Modifier.fillMaxWidth(DesktopMetrics.landscapeHeaderFraction) else Modifier.fillMaxWidth())
                .padding(top = if (landscape) StatusBarLayout.height + DesktopMetrics.windowGap else 0.dp)
                .onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
                .onGloballyPositioned { headerBounds = it.boundsInWindow() }
                .graphicsLayer {
                    alpha = chromeAlpha
                    translationX = -tilt.value.x * headerDrift
                    translationY = direction * (1f - fade.value) * clockLift - tilt.value.y * headerDrift
                    val scale = lerp(DesktopMetrics.overviewChromeScale, 1f, chrome)
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        ) {
            CompositionLocalProvider(LocalWallpaperInk provides headerInk) {
                Column {
                    ClockHeader(sideInset = taskbarEdge)
                    GlanceHost(horizontalInset = taskbarEdge)
                }
            }
            HomeEditDone(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = taskbarEdge)
            )
        }
        CompositionLocalProvider(LocalWallpaperInk provides gridInk) {
        HomeScreenHost(
            onAppLaunched = appLaunched,
            onOverviewChange = { homeOverview = it },
            contentPadding = PaddingValues(
                start = if (landscape) screenWidth * DesktopMetrics.landscapeHeaderFraction else taskbarEdge,
                end = if (landscape) aboveTaskbar else taskbarEdge
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (landscape) landscapeGridMargin else headerHeight + DesktopMetrics.windowGap,
                    bottom = if (landscape) landscapeGridMargin else aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap
                )
                .onGloballyPositioned { gridBounds = it.boundsInWindow() }
                .graphicsLayer {
                    alpha = fade.value
                    translationX = -tilt.value.x * gridDrift
                    translationY = direction * (1f - fade.value) * clockLift - tilt.value.y * gridDrift
                }
        )
        }
    }
    TaskbarHost(
        startOpen = startShown,
        swipeUp = openStartBySwipe,
        onStartClick = {
            startOpen = !startOpen
            controlOpen = false
        },
        onAppLaunched = appLaunched,
        visible = !barsHidden && !homeOverview,
        alpha = chromeAlpha
    )
    StatusBarHost(
        offsetX = taskbarEdge,
        offsetY = if (landscape) pillTop else aboveTaskbar,
        visible = !startShown && !barsHidden && !homeOverview,
        alpha = chromeAlpha,
        active = controlOpen && controlsReady,
        onClick = { if (controlsReady) controlOpen = !controlOpen else NotificationShade.expandSettings(activity) },
        fromTop = landscape,
        swipeUp = if (landscape) null else openStartBySwipe
    )
    SearchPillHost(
        offsetX = taskbarEdge,
        offsetY = if (landscape) navigationBottom + DesktopMetrics.windowGap else aboveTaskbar,
        visible = searchAvailable.value && !startShown && !barsHidden && !homeOverview,
        onClick = {
            controlOpen = false
            hiddenFor = DesktopAction.Search
        },
        alpha = chromeAlpha,
        swipeUp = openStartBySwipe
    )
    ControlCenterHost(
        open = controlOpen && !hidden,
        offsetX = taskbarEdge,
        offsetY = if (landscape) belowPill else aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap,
        widthFraction = TaskbarLayout.widthFraction,
        onClose = { controlOpen = false },
        fromTop = landscape,
        onReadyChange = { ready ->
            controlsReady = ready
            if (!ready) controlOpen = false
        }
    )
    StartMenuHost(
        open = startShown && !hidden,
        swipe = startSwipe,
        tabsOffsetY = aboveTaskbar,
        bottomOffset = aboveTaskbar + StatusBarLayout.height + DesktopMetrics.windowGap,
        endOffset = aboveTaskbar,
        onClose = { startOpen = false },
        onAppLaunched = appLaunched
    )
}
