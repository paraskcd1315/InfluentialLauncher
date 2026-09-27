package com.paraskcd.influentiallauncher.homescreen.presentation

import android.graphics.Bitmap
import android.graphics.RectF
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.infrastructure.LaunchOrigins
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenPage
import com.paraskcd.influentiallauncher.homescreen.presentation.components.AddPageTile
import com.paraskcd.influentiallauncher.homescreen.presentation.components.HomeAppCell
import com.paraskcd.influentiallauncher.homescreen.presentation.components.PageGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.components.PageOverview
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.DeletePageSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.sheets.RemoveAppSheet
import com.paraskcd.influentiallauncher.homescreen.presentation.state.HomeDragState
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics
import com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels.HomeScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeScreenHost(
    onAppLaunched: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val wiggling by viewModel.wiggling.collectAsStateWithLifecycle()
    val overview by viewModel.overview.collectAsStateWithLifecycle()
    val removing by viewModel.removing.collectAsStateWithLifecycle()
    val deleting by viewModel.deleting.collectAsStateWithLifecycle()
    val current = state ?: return
    val pages = current.pages
    val tint = InfTheme.colors.brandText.toArgb()
    val loadIcon: suspend (AppId, Int) -> Bitmap? = remember(tint) { { id, px -> viewModel.icon(id, px, tint) } }
    val pageCount = pages.size + if (wiggling) 1 else 0
    val pager = rememberPagerState(initialPage = current.homeIndex) { pageCount }
    val drag = remember { HomeDragState() }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val view = LocalView.current
    val density = LocalDensity.current
    var areaOrigin by remember { mutableStateOf(Offset.Zero) }
    var areaSize by remember { mutableStateOf(IntSize.Zero) }
    val latestPages by rememberUpdatedState(pages)
    val latestWiggling by rememberUpdatedState(wiggling)
    val latestOverview by rememberUpdatedState(overview)
    val wiggle = wiggleAngle(wiggling)

    BackHandler(enabled = wiggling || overview) {
        viewModel.stopWiggle()
        viewModel.closeOverview()
    }

    val gestures = HomeGestures(
        pager = pager,
        drag = drag,
        scope = scope,
        pages = { latestPages },
        wiggling = { latestWiggling },
        overview = { latestOverview },
        onTapApp = { app, cell ->
            val bounds = RectF(areaOrigin.x + cell.left, areaOrigin.y + cell.top, areaOrigin.x + cell.right, areaOrigin.y + cell.bottom)
            onAppLaunched()
            viewModel.launch(app.id, LaunchOrigins.scaleUp(view, bounds))
        },
        onTapEmpty = { viewModel.stopWiggle() },
        onTapAddPage = { viewModel.addPage() },
        onLongPressApp = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.startWiggle()
        },
        onLongPressEmpty = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.openOverview()
        },
        onDrop = { app, pageIndex, index ->
            val page = latestPages.getOrNull(pageIndex)
            if (page == null) viewModel.moveToNewPage(app.id) else viewModel.move(app.id, page.id, index)
        }
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned {
                    areaOrigin = it.positionInWindow()
                    areaSize = it.size
                }
                .pointerInput(Unit) { with(gestures) { detect() } }
        ) {
            if (overview) {
                PageOverview(
                    pages = pages,
                    homeIndex = current.homeIndex,
                    currentIndex = pager.currentPage,
                    onOpen = { index ->
                        viewModel.closeOverview()
                        scope.launch { pager.scrollToPage(index) }
                    },
                    onSetHome = { viewModel.setHome(it.id) },
                    onDelete = viewModel::askDelete,
                    onAdd = viewModel::addPage
                )
            } else {
                HorizontalPager(
                    state = pager,
                    userScrollEnabled = !drag.dragging,
                    beyondViewportPageCount = 1,
                    key = { pages.getOrNull(it)?.id ?: AddPageKey }
                ) { index ->
                    val page = pages.getOrNull(index)
                    if (page == null) {
                        AddPageTile()
                    } else {
                        PageGrid(
                            slots = slotsFor(page, index, pager.currentPage, drag),
                            loadIcon = loadIcon,
                            wiggle = wiggle,
                            onRemove = if (wiggling) viewModel::askRemove else null
                        )
                    }
                }
            }
            drag.app?.let { app ->
                val cellWidth = with(density) { (areaSize.width / HomeMetrics.columns).toDp() }
                val cellHeight = with(density) { (areaSize.height / HomeMetrics.rows).toDp() }
                HomeAppCell(
                    app = app,
                    loadIcon = loadIcon,
                    modifier = Modifier
                        .offset { (drag.pointer - drag.grab).let { IntOffset(it.x.roundToInt(), it.y.roundToInt()) } }
                        .size(cellWidth, cellHeight)
                        .graphicsLayer {
                            scaleX = HomeMetrics.liftedScale
                            scaleY = HomeMetrics.liftedScale
                        }
                )
            }
        }
        if (!overview && pageCount > 1) {
            PageDots(count = pageCount, current = pager.currentPage, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    RemoveAppSheet(app = removing, onConfirm = viewModel::confirmRemove, onDismiss = viewModel::cancelRemove)
    DeletePageSheet(
        page = deleting,
        pageNumber = deleting?.let { page -> pages.indexOfFirst { it.id == page.id } + 1 } ?: 0,
        onConfirm = viewModel::confirmDelete,
        onDismiss = viewModel::cancelDelete
    )
}

private fun slotsFor(page: HomeScreenPage, index: Int, currentPage: Int, drag: HomeDragState): List<LauncherApp?> {
    val dragged = drag.app ?: return page.apps
    val base: List<LauncherApp?> = page.apps.filterNot { it.id == dragged.id }
    if (index != currentPage) return base
    return base.toMutableList().apply { add(drag.hoverIndex.coerceIn(0, size), null) }
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
                    .background(Color.White.copy(alpha = if (index == current) 1f else DotIdleAlpha))
            )
        }
    }
}

private class HomeGestures(
    private val pager: PagerState,
    private val drag: HomeDragState,
    private val scope: CoroutineScope,
    private val pages: () -> List<HomeScreenPage>,
    private val wiggling: () -> Boolean,
    private val overview: () -> Boolean,
    private val onTapApp: (LauncherApp, RectF) -> Unit,
    private val onTapEmpty: () -> Unit,
    private val onTapAddPage: () -> Unit,
    private val onLongPressApp: () -> Unit,
    private val onLongPressEmpty: () -> Unit,
    private val onDrop: (LauncherApp, Int, Int) -> Unit
) {
    suspend fun PointerInputScope.detect() {
        val edge = HomeMetrics.edgeZone.toPx()
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            var last = down.position
            val outcome = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: return@withTimeoutOrNull Outcome.Cancel
                    last = change.position
                    if (change.changedToUpIgnoreConsumed()) {
                        return@withTimeoutOrNull if (change.isConsumed) Outcome.Cancel else Outcome.Tap
                    }
                    if (change.isConsumed || (change.position - down.position).getDistance() > viewConfiguration.touchSlop) {
                        return@withTimeoutOrNull Outcome.Cancel
                    }
                }
                @Suppress("UNREACHABLE_CODE")
                Outcome.Cancel
            }
            if (overview() || outcome == Outcome.Cancel) return@awaitEachGesture
            val pageIndex = pager.currentPage
            val page = pages().getOrNull(pageIndex)
            val slot = slotAt(last, size)
            val app = page?.apps?.getOrNull(slot)
            if (outcome == Outcome.Tap) {
                when {
                    page == null -> onTapAddPage()
                    app != null && !wiggling() -> onTapApp(app, cellRect(slot, size))
                    app == null && wiggling() -> onTapEmpty()
                }
                return@awaitEachGesture
            }
            if (page == null) return@awaitEachGesture
            if (app == null) {
                onLongPressEmpty()
                return@awaitEachGesture
            }
            onLongPressApp()
            val cell = cellRect(slot, size)
            drag.start(app, last, Offset(cell.left, cell.top), slot)
            var edgeSide = 0
            var edgeJob: Job? = null
            try {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id } ?: break
                    val position = change.position
                    val released = !change.pressed
                    event.changes.forEach { it.consume() }
                    if (released) {
                        onDrop(app, pager.currentPage, drag.hoverIndex.coerceAtLeast(0))
                        break
                    }
                    drag.move(position, size, countOn(pager.currentPage, app))
                    val side = when {
                        position.x < edge -> -1
                        position.x > size.width - edge -> 1
                        else -> 0
                    }
                    if (side != edgeSide) {
                        edgeJob?.cancel()
                        edgeSide = side
                        edgeJob = if (side == 0) null else scope.launch { flipPages(side, app) }
                    }
                }
            } finally {
                edgeJob?.cancel()
                drag.end()
            }
        }
    }

    private suspend fun flipPages(side: Int, app: LauncherApp) {
        while (true) {
            delay(HomeMetrics.edgePageMs)
            val target = pager.currentPage + side
            if (target !in 0 until pager.pageCount) return
            pager.animateScrollToPage(target)
            drag.clampHover(countOn(target, app))
        }
    }

    private fun countOn(pageIndex: Int, app: LauncherApp): Int =
        pages().getOrNull(pageIndex)?.apps?.count { it.id != app.id } ?: 0

    private fun slotAt(position: Offset, area: IntSize): Int {
        val column = (position.x / (area.width / HomeMetrics.columns.toFloat())).toInt().coerceIn(0, HomeMetrics.columns - 1)
        val row = (position.y / (area.height / HomeMetrics.rows.toFloat())).toInt().coerceIn(0, HomeMetrics.rows - 1)
        return row * HomeMetrics.columns + column
    }

    private fun cellRect(slot: Int, area: IntSize): RectF {
        val width = area.width / HomeMetrics.columns.toFloat()
        val height = area.height / HomeMetrics.rows.toFloat()
        val left = (slot % HomeMetrics.columns) * width
        val top = (slot / HomeMetrics.columns) * height
        return RectF(left, top, left + width, top + height)
    }

    private enum class Outcome { Tap, Cancel }
}

private const val AddPageKey = "add"
private const val DotIdleAlpha = 0.4f
