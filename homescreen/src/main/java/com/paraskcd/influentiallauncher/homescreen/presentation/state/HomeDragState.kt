package com.paraskcd.influentiallauncher.homescreen.presentation.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.DragSource
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Stable
class HomeDragState {
    var payload by mutableStateOf<AppDragPayload?>(null)
        private set
    var hoverIndex by mutableIntStateOf(-1)
        private set

    val app: LauncherApp? get() = payload?.app
    val dragging: Boolean get() = payload != null
    val fromHome: Boolean get() = payload?.source == DragSource.Home

    fun begin(dragged: AppDragPayload) {
        payload = dragged
        hoverIndex = -1
    }

    fun move(to: Offset, area: IntSize, count: Int) {
        val cellWidth = area.width / HomeMetrics.columns.toFloat()
        val cellHeight = area.height / HomeMetrics.rows.toFloat()
        if (cellWidth <= 0f || cellHeight <= 0f) return
        val column = (to.x / cellWidth).toInt().coerceIn(0, HomeMetrics.columns - 1)
        val row = (to.y / cellHeight).toInt().coerceIn(0, HomeMetrics.rows - 1)
        val insideX = (to.x - column * cellWidth) / cellWidth
        val insideY = (to.y - row * cellHeight) / cellHeight
        val margin = (1f - HomeMetrics.hoverCore) / 2f
        val nearEdge = insideX < margin || insideX > 1f - margin || insideY < margin || insideY > 1f - margin
        if (nearEdge && hoverIndex >= 0) return
        hoverIndex = (row * HomeMetrics.columns + column).coerceIn(0, count)
    }

    fun clampHover(count: Int) {
        if (hoverIndex > count) hoverIndex = count
    }

    fun leave() {
        hoverIndex = -1
    }

    fun end() {
        payload = null
        hoverIndex = -1
    }
}
