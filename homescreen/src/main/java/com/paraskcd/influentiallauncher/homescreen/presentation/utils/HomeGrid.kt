package com.paraskcd.influentiallauncher.homescreen.presentation.utils

import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout

/** The columns and rows one home page lays its apps out in, for the current orientation. */
data class HomeGrid(val columns: Int, val rows: Int) {
    init {
        require(columns * rows == HomeLayout.PageCapacity)
    }

    companion object {
        val Portrait = HomeGrid(columns = 4, rows = 6)
        val Landscape = HomeGrid(columns = 6, rows = 4)

        fun of(landscape: Boolean): HomeGrid = if (landscape) Landscape else Portrait
    }
}
