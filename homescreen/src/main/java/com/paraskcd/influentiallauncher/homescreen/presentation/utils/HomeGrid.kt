// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.utils

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize

/** The columns and rows one visual home page shows. */
data class HomeGrid(val columns: Int, val rows: Int) {
    val capacity: Int get() = columns * rows

    companion object {
        private val Portrait = HomeGrid(columns = 4, rows = 5)
        private val Landscape = HomeGrid(columns = 6, rows = 4)

        fun preferred(landscape: Boolean): HomeGrid = if (landscape) Landscape else Portrait

        fun fit(area: IntSize, density: Density, landscape: Boolean): HomeGrid {
            val preferred = preferred(landscape)
            if (area.width <= 0 || area.height <= 0) return preferred
            val (minWidth, minHeight) = with(density) { HomeMetrics.minCellWidth.toPx() to HomeMetrics.minCellHeight.toPx() }
            return HomeGrid(
                columns = (area.width / minWidth).toInt().coerceIn(1, preferred.columns),
                rows = (area.height / minHeight).toInt().coerceIn(1, preferred.rows)
            )
        }
    }
}
