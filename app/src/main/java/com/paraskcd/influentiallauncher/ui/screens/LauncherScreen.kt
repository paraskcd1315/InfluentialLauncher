package com.paraskcd.influentiallauncher.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.paraskcd.influentiallauncher.ui.dialogs.DockDialog
import com.paraskcd.influentiallauncher.ui.dialogs.WeatherMediaDialog
import com.paraskcd.influentiallauncher.ui.components.ClockHeader
import com.paraskcd.influentiallauncher.ui.components.HomeGrid
import com.paraskcd.influentiallauncher.ui.components.Statusbar.Statusbar
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import com.paraskcd.influentiallauncher.viewmodels.LauncherStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LauncherScreen(navController: NavController, launcherState: LauncherStateViewModel, viewModel: LauncherItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val blur = remember { Animatable(0f) }

    val weatherDialogHeightPx by WeatherMediaDialog.dialogHeightFlow.collectAsState(0)
    val weatherDialogHeightDp = with(density) { weatherDialogHeightPx.toDp() }
    val spacerHeight by animateDpAsState(targetValue = weatherDialogHeightDp, label = "wm-spacer")

    var autoPageConsumed by remember { mutableStateOf(false) }

    LaunchedEffect(blur.value) {
        activity?.window?.setBackgroundBlurRadius(blur.value.toInt())
    }

    LaunchedEffect(Unit) {
        DockDialog.showOrUpdate(context = context)
        WeatherMediaDialog.showOrUpdate(context = context)
    }

    val triggerDownPx = with(density) { 220.dp.toPx() }
    val triggerUpPx = with(density) { 220.dp.toPx() }

    val dockHeightPx by DockDialog.dialogHeightFlow.collectAsState(0)
    val dockHeightDp = with(density) { dockHeightPx.toDp() }
    val dockAnimatedHeight by animateDpAsState(targetValue = dockHeightDp, label = "dock-height")

    var downAccum by remember { mutableFloatStateOf(0f) }
    var upAccum by remember { mutableFloatStateOf(0f) }

    val statusBarTop = WindowInsets
        .statusBarsIgnoringVisibility
        .asPaddingValues()
        .calculateTopPadding()
    val homeItems by viewModel.home.collectAsState()
    val screens by launcherState.screens.collectAsState()
    val activeIndex by launcherState.activeScreenIndex.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = activeIndex.coerceAtLeast(0),
        pageCount = { screens.size.coerceAtLeast(1) }
    )
    val homeEditMode by viewModel.homeEditMode.collectAsState()

    LaunchedEffect(activeIndex, screens.size) {
        val target = activeIndex.coerceIn(0, (screens.size - 1).coerceAtLeast(0))
        if (pagerState.currentPage != target && target >= 0) {
            pagerState.scrollToPage(target)
        }
    }

    LaunchedEffect(pagerState, screens) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                val id = screens.getOrNull(page)?.id ?: return@collectLatest
                launcherState.setActiveScreen(id, persistAsDefault = false)
            }
    }

    val commonPaddingModifier = Modifier.padding(start = 48.dp, end = 48.dp)
    var isDragging by remember { mutableStateOf(false) }
    var draggingItemId by remember { mutableStateOf<Long?>(null) }
    var edgeJob by remember { mutableStateOf<Job?>(null) }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val edgeThresholdPx = with(density) { 48.dp.toPx() }
    var dragSession by remember { mutableStateOf(0) }

    fun findFirstEmptyCell(screenId: Long?): Pair<Int, Int>? {
        val itemsForScreen = homeItems.filter { it.screenId == screenId }
        val occupied = itemsForScreen.mapNotNull { e ->
            val r = e.row; val c = e.column
            if (r != null && c != null) r to c else null
        }.toSet()
        val rows = 5; val cols = 4
        for (r in 0 until rows) for (c in 0 until cols) {
            if ((r to c) !in occupied) return r to c
        }
        return null
    }

    fun scheduleAutoPage(xOnScreen: Float) {
        val id = draggingItemId ?: return
        if (!homeEditMode || screens.isEmpty()) return

        // Si ya saltó, solo cancela si sales del borde
        if (autoPageConsumed) {
            val neutral = xOnScreen > edgeThresholdPx && xOnScreen < screenWidthPx - edgeThresholdPx
            if (neutral) {
                edgeJob?.cancel()
                edgeJob = null
            }
            return
        }

        val cur = pagerState.currentPage
        val goLeft = xOnScreen <= edgeThresholdPx && cur > 0
        val goRight = xOnScreen >= screenWidthPx - edgeThresholdPx && cur < screens.lastIndex

        // No en borde: cancela
        if (!goLeft && !goRight) {
            edgeJob?.cancel()
            edgeJob = null
            return
        }
        if (edgeJob?.isActive == true) return

        val mySession = dragSession
        edgeJob = scope.launch {
            // Dwell para evitar falsos positivos
            delay(250)
            // Si cambió la sesión o ya se consumió, aborta
            if (dragSession != mySession || autoPageConsumed) return@launch

            val targetPage = if (goLeft) cur - 1 else cur + 1
            val targetScreen = screens.getOrNull(targetPage) ?: return@launch

            // Coloca provisionalmente en la primera celda libre de la página destino
            val empty = findFirstEmptyCell(targetScreen.id) ?: (0 to 0)
            viewModel.moveHome(id, targetScreen.id, empty.first, empty.second)

            // Marca como consumido y navega una sola página
            autoPageConsumed = true
            pagerState.animateScrollToPage(targetPage)
            edgeJob = null
        }
    }

    LaunchedEffect(homeEditMode) {
        if (!homeEditMode) {
            draggingItemId = null
            isDragging = false
        }
    }

    BackHandler(enabled = true) { }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal)
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .then(
                    if (!homeEditMode) {
                        Modifier.pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    downAccum = 0f
                                    upAccum = 0f
                                },
                                onVerticalDrag = { _, dy ->
                                    if (dy > 0f) {
                                        downAccum = (downAccum + dy).coerceAtLeast(0f)
                                        upAccum = 0f
                                    } else if (dy < 0f) {
                                        upAccum = (upAccum + -dy).coerceAtLeast(0f)
                                        downAccum = 0f
                                    }
                                    val pDown = (downAccum / triggerDownPx).coerceIn(0f, 1f)
                                    val pUp = (upAccum / triggerUpPx).coerceIn(0f, 1f)
                                    val p = maxOf(pDown, pUp)
                                    scope.launch { blur.snapTo(p * 100f) }
                                },
                                onDragEnd = {
                                    val pDown = (downAccum / triggerDownPx).coerceIn(0f, 1f)
                                    val pUp = (upAccum / triggerUpPx).coerceIn(0f, 1f)

                                    when {
                                        pUp >= 0.7f -> scope.launch {
                                            blur.animateTo(100f, tween(120))
                                            val intent = Intent().apply {
                                                setClassName(
                                                    "com.paraskcd.spotlightsearch",
                                                    "com.paraskcd.spotlightsearch.MainActivity"
                                                )
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            val opts = ActivityOptions.makeCustomAnimation(
                                                context,
                                                android.R.anim.fade_in,
                                                android.R.anim.fade_out
                                            )
                                            context.startActivity(intent, opts.toBundle())
                                            delay(220)
                                            blur.snapTo(0f)
                                            activity?.window?.setBackgroundBlurRadius(0)
                                        }

                                        pDown >= 0.7f -> scope.launch {
                                            blur.animateTo(100f, tween(100))
                                            val opened = viewModel.openNotifications()
                                            delay(if (opened) 220 else 120)
                                            blur.animateTo(0f, tween(150))
                                            activity?.window?.setBackgroundBlurRadius(0)
                                        }

                                        else -> scope.launch { blur.animateTo(0f, tween(150)) }
                                    }
                                },
                                onDragCancel = {
                                    scope.launch { blur.animateTo(0f, tween(150)) }
                                }
                            )
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            val scrimAlpha = (blur.value / 100f) * 0.15f
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                navController.navigate("screen_manager") {
                                    launchSingleTop = true
                                }
                                WeatherMediaDialog.close()
                                DockDialog.close()
                                activity?.window?.setBackgroundBlurRadius(100)
                            }
                        )
                    }
            )

            Column(
                modifier = Modifier
                    .padding(top = statusBarTop + 32.dp, bottom = dockAnimatedHeight + 50.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = commonPaddingModifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClockHeader()
                    if (homeEditMode) {
                        Button(onClick = { viewModel.setHomeEditMode(false) }) {
                            Text("Done")
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f, fill = false)
                        .combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onLongClick = {
                                navController.navigate("screen_manager") { launchSingleTop = true }
                                WeatherMediaDialog.close()
                                DockDialog.close()
                                activity?.window?.setBackgroundBlurRadius(100)
                            },
                            onClick = {} // evita click normal
                        ),
                    userScrollEnabled = !isDragging
                ) { page ->
                    val screenId = screens.getOrNull(page)?.id
                    HomeGrid(
                        rows = 5,
                        columns = 4,
                        items = homeItems,
                        currentScreenId = screenId,
                        viewModel = viewModel,
                        modifier = commonPaddingModifier,
                        onDragStart = {
                            draggingItemId = it
                            isDragging = true
                            autoPageConsumed = false
                            dragSession += 1
                            edgeJob?.cancel()
                            edgeJob = null
                        },
                        onDragMoveXInWindow = { x -> scheduleAutoPage(x) },
                        onDropOnCell = { itemId, r, c ->
                            val screenId = screens.getOrNull(pagerState.currentPage)?.id ?: return@HomeGrid
                            viewModel.moveHome(itemId, screenId, r, c)
                            draggingItemId = null
                            autoPageConsumed = false
                            edgeJob?.cancel()
                            edgeJob = null
                        }
                    )
                }
                Spacer(modifier = Modifier.height(spacerHeight))
                Statusbar(
                    context = context,
                    viewModel = viewModel,
                    modifier = commonPaddingModifier
                )
            }

        }
    }
}