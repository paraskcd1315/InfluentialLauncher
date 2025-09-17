package com.paraskcd.influentiallauncher.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.runtime.derivedStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LauncherScreen(navController: NavController, launcherState: LauncherStateViewModel, viewModel: LauncherItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val blur = remember { Animatable(0f) }

    val weatherDialogHeightPx by WeatherMediaDialog.dialogHeightFlow.collectAsState(0)
    val weatherDialogHeightDp = with(density) { weatherDialogHeightPx.toDp() }
    val spacerHeight by animateDpAsState(targetValue = weatherDialogHeightDp, label = "wm-spacer")

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
                launcherState.setActiveScreenByIndex(page, persistAsDefault = false)
            }
    }

    val commonPaddingModifier = Modifier.padding(start = 48.dp, end = 48.dp)

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
                        .weight(1f, fill = false),
                    userScrollEnabled = !homeEditMode
                ) { page ->
                    val screenId = screens.getOrNull(page)?.id
                    HomeGrid(
                        rows = 5,
                        columns = 4,
                        items = homeItems,
                        currentScreenId = screenId,
                        viewModel = viewModel,
                        modifier = commonPaddingModifier
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