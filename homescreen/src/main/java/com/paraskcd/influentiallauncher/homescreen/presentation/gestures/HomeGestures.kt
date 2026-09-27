package com.paraskcd.influentiallauncher.homescreen.presentation.gestures

import android.graphics.RectF
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.unit.IntSize
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenPage
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

class HomeGestures(
    private val pager: PagerState,
    private val pages: () -> List<HomeScreenPage>,
    private val wiggling: () -> Boolean,
    private val overview: () -> Boolean,
    private val onTapApp: (LauncherApp, RectF) -> Unit,
    private val onTapEmpty: () -> Unit,
    private val onTapAddPage: () -> Unit,
    private val onMenu: (LauncherApp) -> Unit,
    private val onDragApp: (LauncherApp) -> Unit,
    private val onLongPressEmpty: () -> Unit
) {
    suspend fun PointerInputScope.detect() {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            if (overview()) return@awaitEachGesture
            val page = pages().getOrNull(pager.currentPage)
            val slot = slotAt(down.position, size)
            val app = page?.apps?.getOrNull(slot)
            val outcome = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) {
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull { it.id == down.id } ?: return@withTimeoutOrNull Outcome.Cancel
                    if (change.changedToUpIgnoreConsumed()) {
                        return@withTimeoutOrNull if (change.isConsumed) Outcome.Cancel else Outcome.Tap
                    }
                    val moved = (change.position - down.position).getDistance() > viewConfiguration.touchSlop
                    if (moved && app != null && wiggling()) return@withTimeoutOrNull Outcome.Drag
                    if (change.isConsumed || moved) return@withTimeoutOrNull Outcome.Cancel
                }
                @Suppress("UNREACHABLE_CODE")
                Outcome.Cancel
            } ?: Outcome.LongPress
            when (outcome) {
                Outcome.Cancel -> Unit
                Outcome.Drag -> app?.let(onDragApp)
                Outcome.Tap -> when {
                    page == null -> onTapAddPage()
                    app != null && !wiggling() -> onTapApp(app, cellRect(slot, size))
                    app == null && wiggling() -> onTapEmpty()
                }
                Outcome.LongPress -> when {
                    page == null -> Unit
                    app == null -> onLongPressEmpty()
                    wiggling() -> onDragApp(app)
                    else -> onMenu(app)
                }
            }
            if (outcome == Outcome.LongPress) {
                do {
                    val event = awaitPointerEvent()
                    event.changes.forEach { it.consume() }
                } while (event.changes.any { it.pressed })
            }
        }
    }

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

    private enum class Outcome { Tap, Drag, LongPress, Cancel }
}
