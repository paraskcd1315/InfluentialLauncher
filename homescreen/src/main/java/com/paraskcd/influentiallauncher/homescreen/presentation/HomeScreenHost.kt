package com.paraskcd.influentiallauncher.homescreen.presentation

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.infrastructure.LaunchOrigins
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.presentation.components.AddPageTile
import com.paraskcd.influentiallauncher.homescreen.presentation.components.PageGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.components.PageOverview
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDrag
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.DragSource
import com.paraskcd.influentiallauncher.homescreen.presentation.gestures.GridInsets
import com.paraskcd.influentiallauncher.homescreen.presentation.gestures.HomeGestures
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.DeletePageSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.HomeAppSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.RemoveAppSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.state.HomeDragState
import com.paraskcd.influentiallauncher.homescreen.presentation.state.HomePaging
import com.paraskcd.influentiallauncher.homescreen.presentation.state.VisualPage
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics
import com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels.HomeScreenViewModel
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalParallax
import com.paraskcd.influentiallauncher.homescreen.presentation.state.HomeIconSpots
import com.paraskcd.influentiallauncher.homescreen.presentation.windows.HomeIconWindows
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** The edge-to-edge home pager, whose [contentPadding] insets each page's grid. */
@Composable
fun HomeScreenHost(
    onAppLaunched: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onOverviewChange: (Boolean) -> Unit = {},
    iconWindows: Boolean = false,
    iconParallax: Dp = 0.dp,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val wiggling by viewModel.wiggling.collectAsStateWithLifecycle()
    val overview by viewModel.overview.collectAsStateWithLifecycle()
    val menu by viewModel.menu.collectAsStateWithLifecycle()
    val removing by viewModel.removing.collectAsStateWithLifecycle()
    val deleting by viewModel.deleting.collectAsStateWithLifecycle()
    val taskbarIds by viewModel.taskbarIds.collectAsStateWithLifecycle()
    val current = state ?: return
    val pages = current.pages
    val tint = InfTheme.colors.brandText.toArgb()
    val iconBackground = InfTheme.colors.glassStrongBg.toArgb()
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint, iconBackground) { { id, px -> viewModel.icon(id, px, tint, iconBackground) } }
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val landscape = isLandscape()
    var areaOrigin by remember { mutableStateOf(Offset.Zero) }
    var areaSize by remember { mutableStateOf(IntSize.Zero) }
    val insets = with(density) {
        GridInsets(
            startPx = contentPadding.calculateLeftPadding(layoutDirection).toPx(),
            endPx = contentPadding.calculateRightPadding(layoutDirection).toPx(),
            maxCellHeightPx = HomeMetrics.maxCellHeight.toPx()
        )
    }
    val grid = HomeGrid.fit(insets.horizontal(areaSize), density, landscape)
    val visual = remember(pages, grid) { HomePaging.visualPages(pages, grid.capacity) }
    val homeVisual = visual.indexOfFirst { it.page.id == pages.getOrNull(current.homeIndex)?.id }.coerceAtLeast(0)
    val pageCount = visual.size + if (wiggling) 1 else 0
    val pager = rememberPagerState(initialPage = homeVisual) { pageCount }
    val drag = remember { HomeDragState() }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val iconPx = with(density) { HomeMetrics.iconSize.roundToPx() }
    val latestVisual by rememberUpdatedState(visual)
    val latestGrid by rememberUpdatedState(grid)
    val latestInsets by rememberUpdatedState(insets)
    val latestWiggling by rememberUpdatedState(wiggling)
    val latestOverview by rememberUpdatedState(overview)
    val wiggle = wiggleAngle(wiggling)
    val overviewProgress by animateFloatAsState(
        targetValue = if (overview) 1f else 0f,
        animationSpec = tween(HomeMetrics.overviewMs, easing = InfMotion.easeIos),
        label = "homeOverview"
    )
    LaunchedEffect(overview) { onOverviewChange(overview) }
    val tilt = LocalParallax.current
    val iconDriftPx = with(density) { iconParallax.toPx() }
    val spots = remember(tilt, iconDriftPx) { HomeIconSpots { tilt.value * iconDriftPx } }
    val iconsInWindows = iconWindows && !wiggling && !overview && overviewProgress == 0f && !drag.dragging && !pager.isScrollInProgress
    HomeIconWindows(spots = spots, visible = iconsInWindows, parallaxShift = iconParallax, loadIcon = loadIcon)

    BackHandler(enabled = wiggling || overview) {
        viewModel.stopWiggle()
        viewModel.closeOverview()
    }

    val gestures = remember {
        HomeGestures(
            pager = pager,
            pages = { latestVisual },
            grid = { latestGrid },
            insets = { latestInsets },
            wiggling = { latestWiggling },
            overview = { latestOverview },
            onTapApp = { app, cell ->
                val bounds = RectF(areaOrigin.x + cell.left, areaOrigin.y + cell.top, areaOrigin.x + cell.right, areaOrigin.y + cell.bottom)
                onAppLaunched()
                viewModel.launch(app.id, LaunchOrigins.scaleUp(view, bounds))
            },
            onTapEmpty = { viewModel.stopWiggle() },
            onTapAddPage = { viewModel.addPage() },
            onMenu = { app ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.openMenu(app)
            },
            onDragApp = { app ->
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                AppDrag.start(view, AppDragPayload(DragSource.Home, app), viewModel.cachedIcon(app.id, iconPx, tint, iconBackground), iconPx)
            },
            onLongPressEmpty = {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.openOverview()
            }
        )
    }

    val dropTarget = remember {
        HomeDropTarget(
            drag = drag,
            origin = { areaOrigin },
            size = { areaSize },
            grid = { latestGrid },
            insets = { latestInsets },
            edgePx = with(density) { HomeMetrics.edgeZone.toPx() },
            flip = { side ->
                scope.launch {
                    while (true) {
                        delay(HomeMetrics.edgePageMs)
                        val target = pager.currentPage + side
                        if (target !in 0 until pager.pageCount) break
                        pager.animateScrollToPage(target)
                    }
                }
            },
            currentPage = { pager.currentPage },
            onDrop = { payload, pageIndex, index ->
                val page = latestVisual.getOrNull(pageIndex)
                when (payload.source) {
                    DragSource.Home -> if (page == null) viewModel.moveToNewPage(payload.app.id) else viewModel.move(payload.app.id, page.page.id, page.start + index)
                    DragSource.Taskbar -> viewModel.fromTaskbar(payload.app.id, page?.page?.id, (page?.start ?: 0) + index)
                }
            }
        )
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned {
                    areaOrigin = it.positionInWindow()
                    areaSize = it.size
                }
                .dragAndDropTarget(shouldStartDragAndDrop = { AppDrag.payloadOf(it.toAndroidDragEvent()) != null }, target = dropTarget)
                .pointerInput(Unit) { with(gestures) { detect() } }
        ) {
            if (overviewProgress > 0f) {
                PageOverview(
                    pages = visual,
                    homeIndex = homeVisual,
                    currentIndex = pager.currentPage.coerceAtMost(visual.lastIndex),
                    onOpen = { index ->
                        viewModel.closeOverview()
                        scope.launch { pager.scrollToPage(index) }
                    },
                    onSetHome = viewModel::setHome,
                    onDelete = viewModel::askDelete,
                    onAdd = viewModel::addPage,
                    grid = grid,
                    loadIcon = loadIcon,
                    modifier = Modifier
                        .graphicsLayer {
                            val scale = lerp(HomeMetrics.overviewCardsScale, 1f, overviewProgress)
                            scaleX = scale
                            scaleY = scale
                            alpha = overviewProgress
                        }
                )
            }
            if (overviewProgress < 1f) {
                HorizontalPager(
                    state = pager,
                    userScrollEnabled = !drag.dragging && !overview,
                    beyondViewportPageCount = 1,
                    key = { visual.getOrNull(it)?.key ?: AddPageKey },
                    modifier = Modifier.graphicsLayer {
                        val scale = lerp(1f, HomeMetrics.overviewPagerScale, overviewProgress)
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - overviewProgress
                    }
                ) { index ->
                    val page = visual.getOrNull(index)
                    Box(modifier = Modifier.padding(contentPadding)) {
                        if (page == null) {
                            AddPageTile()
                        } else {
                            PageGrid(
                                slots = HomePaging.gridSlots(page, grid.capacity, drag, hovered = index == pager.currentPage),
                                grid = grid,
                                loadIcon = loadIcon,
                                wiggle = wiggle,
                                onRemove = if (wiggling) viewModel::askRemove else null,
                                spots = spots
                            )
                        }
                    }
                }
            }
        }
        if (!overview && pageCount > 1) {
            PageDots(count = pageCount, current = pager.currentPage, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    HomeAppSheet(
        app = menu,
        onTaskbar = menu?.id in taskbarIds,
        loadIcon = loadIcon,
        onEdit = viewModel::startWiggle,
        onToggleTaskbar = viewModel::toggleTaskbar,
        onRemove = viewModel::askRemove,
        onInfo = {
            onAppLaunched()
            viewModel.openInfo(it)
        },
        onUninstall = {
            onAppLaunched()
            viewModel.uninstall(it)
        },
        onDismiss = viewModel::closeMenu
    )
    RemoveAppSheet(app = removing, onConfirm = viewModel::confirmRemove, onDismiss = viewModel::cancelRemove)
    DeletePageSheet(
        page = deleting,
        pageNumber = deleting?.let { screen -> visual.indexOfFirst { it.key == screen.key } + 1 } ?: 0,
        onConfirm = viewModel::confirmDelete,
        onDismiss = viewModel::cancelDelete
    )
}

private class HomeDropTarget(
    private val drag: HomeDragState,
    private val origin: () -> Offset,
    private val size: () -> IntSize,
    private val grid: () -> HomeGrid,
    private val insets: () -> GridInsets,
    private val edgePx: Float,
    private val flip: (Int) -> Job,
    private val currentPage: () -> Int,
    private val onDrop: (AppDragPayload, Int, Int) -> Unit
) : DragAndDropTarget {
    private var edgeSide = 0
    private var edgeJob: Job? = null

    override fun onStarted(event: DragAndDropEvent) {
        AppDrag.payloadOf(event.toAndroidDragEvent())?.let(drag::begin)
    }

    override fun onMoved(event: DragAndDropEvent) {
        val android = event.toAndroidDragEvent()
        val full = Offset(android.x, android.y) - origin()
        val inset = insets()
        val shape = grid()
        drag.move(inset.local(full, size(), shape), inset.block(size(), shape), shape)
        val side = when {
            full.x < edgePx -> -1
            full.x > size().width - edgePx -> 1
            else -> 0
        }
        if (side != edgeSide) {
            edgeJob?.cancel()
            edgeSide = side
            edgeJob = if (side == 0) null else flip(side)
        }
    }

    override fun onExited(event: DragAndDropEvent) {
        stopFlip()
        drag.leave()
    }

    override fun onDrop(event: DragAndDropEvent): Boolean {
        val payload = AppDrag.payloadOf(event.toAndroidDragEvent()) ?: return false
        stopFlip()
        onDrop(payload, currentPage(), drag.hoverIndex.coerceAtLeast(0))
        return true
    }

    override fun onEnded(event: DragAndDropEvent) {
        stopFlip()
        drag.end()
    }

    private fun stopFlip() {
        edgeJob?.cancel()
        edgeJob = null
        edgeSide = 0
    }
}

@Composable
private fun wiggleAngle(active: Boolean): Float {
    if (!active) return 0f
    val transition = rememberInfiniteTransition(label = "wiggle")
    val angle by transition.animateFloat(
        initialValue = -HomeMetrics.wiggleDegrees,
        targetValue = HomeMetrics.wiggleDegrees,
        animationSpec = infiniteRepeatable(tween(HomeMetrics.wiggleMs), RepeatMode.Reverse),
        label = "wiggleAngle"
    )
    return angle
}

@Composable
private fun PageDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(HomeMetrics.dotGap),
        modifier = modifier.padding(top = HomeMetrics.dotsGap)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(HomeMetrics.dot)
                    .clip(CircleShape)
                    .background(LocalWallpaperInk.current.content.copy(alpha = if (index == current) 1f else DotIdleAlpha))
            )
        }
    }
}

private const val AddPageKey = "add"
private const val DotIdleAlpha = 0.4f
