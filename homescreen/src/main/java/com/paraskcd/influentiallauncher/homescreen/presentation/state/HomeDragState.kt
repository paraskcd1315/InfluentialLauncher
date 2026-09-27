package com.paraskcd.influentiallauncher.homescreen.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Stable
class HomeDragState {
    var app by mutableStateOf<LauncherApp?>(null)
        private set
    var pointer by mutableStateOf(Offset.Zero)
        private set
    var grab by mutableStateOf(Offset.Zero)
        private set
    var hoverIndex by mutableIntStateOf(-1)
        private set

    val dragging: Boolean get() = app != null

    fun start(picked: LauncherApp, at: Offset, cellTopLeft: Offset, fromIndex: Int) {
        app = picked
        pointer = at
        grab = at - cellTopLeft
        hoverIndex = fromIndex
    }

    fun move(to: Offset, area: IntSize, count: Int) {
        pointer = to
        val cellWidth = area.width / HomeMetrics.columns.toFloat()
        val cellHeight = area.height / HomeMetrics.rows.toFloat()
        if (cellWidth <= 0f || cellHeight <= 0f) return
        val column = (to.x / cellWidth).toInt().coerceIn(0, HomeMetrics.columns - 1)
        val row = (to.y / cellHeight).toInt().coerceIn(0, HomeMetrics.rows - 1)
        val insideX = (to.x - column * cellWidth) / cellWidth
        val insideY = (to.y - row * cellHeight) / cellHeight
        val margin = (1f - HomeMetrics.hoverCore) / 2f
        if (insideX < margin || insideX > 1f - margin || insideY < margin || insideY > 1f - margin) return
        hoverIndex = (row * HomeMetrics.columns + column).coerceIn(0, count)
    }

    fun clampHover(count: Int) {
        if (hoverIndex > count) hoverIndex = count
    }

    fun end() {
        app = null
        hoverIndex = -1
    }
}
