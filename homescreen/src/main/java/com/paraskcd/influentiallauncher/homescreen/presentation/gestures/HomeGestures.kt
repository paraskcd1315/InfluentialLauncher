// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

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
import com.paraskcd.influentiallauncher.homescreen.presentation.state.VisualPage
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeGrid

class HomeGestures(
    private val pager: PagerState,
    private val pages: () -> List<VisualPage>,
    private val grid: () -> HomeGrid,
    private val insets: () -> GridInsets,
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
            val inset = insets()
            val shape = grid()
            val area = inset.block(size, shape)
            val local = inset.local(down.position, size, shape)
            val slot = slotAt(local, area)
            val app = slot?.let { page?.slots?.getOrNull(it) }
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
                    app != null && slot != null && !wiggling() -> onTapApp(app, inset.window(cellRect(slot, area), size, shape))
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

    private fun slotAt(position: Offset, area: IntSize): Int? {
        if (position.x < 0f || position.x >= area.width || position.y < 0f || position.y >= area.height) return null
        val shape = grid()
        val column = (position.x / (area.width / shape.columns.toFloat())).toInt().coerceIn(0, shape.columns - 1)
        val row = (position.y / (area.height / shape.rows.toFloat())).toInt().coerceIn(0, shape.rows - 1)
        return row * shape.columns + column
    }

    private fun cellRect(slot: Int, area: IntSize): RectF {
        val shape = grid()
        val width = area.width / shape.columns.toFloat()
        val height = area.height / shape.rows.toFloat()
        val left = (slot % shape.columns) * width
        val top = (slot / shape.columns) * height
        return RectF(left, top, left + width, top + height)
    }

    private enum class Outcome { Tap, Drag, LongPress, Cancel }
}

/** Where the grid block sits inside the full-width pager: side padding, and vertically centred rows of capped height. */
data class GridInsets(val startPx: Float, val endPx: Float, val maxCellHeightPx: Float) {
    fun horizontal(full: IntSize): IntSize = IntSize((full.width - startPx - endPx).toInt().coerceAtLeast(0), full.height)

    fun block(full: IntSize, grid: HomeGrid): IntSize {
        val cellHeight = minOf(full.height / grid.rows.toFloat(), maxCellHeightPx)
        return IntSize(horizontal(full).width, (cellHeight * grid.rows).toInt())
    }

    fun top(full: IntSize, grid: HomeGrid): Float = (full.height - block(full, grid).height) / 2f

    fun local(position: Offset, full: IntSize, grid: HomeGrid): Offset = position - Offset(startPx, top(full, grid))

    fun window(cell: RectF, full: IntSize, grid: HomeGrid): RectF {
        val top = top(full, grid)
        return RectF(cell.left + startPx, cell.top + top, cell.right + startPx, cell.bottom + top)
    }
}
