package com.paraskcd.influentiallauncher.homescreen.presentation.state

import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenPage
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.SlotPlacement

/** One screen of the pager: a slice of a user page's slots, starting at [start]. */
data class VisualPage(val page: HomeScreenPage, val start: Int, val slots: List<LauncherApp?>) {
    val key: String get() = "${page.id}:$start"
}

sealed interface GridSlot {
    data class App(val app: LauncherApp) : GridSlot
    data object Empty : GridSlot
    data object Target : GridSlot
}

object HomePaging {
    fun visualPages(pages: List<HomeScreenPage>, capacity: Int): List<VisualPage> = pages.flatMap { page ->
        val screens = maxOf(1, (page.apps.size + capacity - 1) / capacity)
        (0 until screens).map { screen ->
            val start = screen * capacity
            VisualPage(page, start, page.apps.drop(start).take(capacity))
        }
    }

    fun gridSlots(visual: VisualPage, capacity: Int, drag: HomeDragState, hovered: Boolean): List<GridSlot> {
        val dragged = drag.app
        val lifted: List<GridSlot?> = visual.page.apps.map { app ->
            when {
                app == null -> null
                dragged != null && drag.fromHome && app.id == dragged.id -> null
                else -> GridSlot.App(app)
            }
        }
        val placed = if (dragged != null && hovered && drag.hoverIndex >= 0) {
            SlotPlacement.place(lifted, visual.start + drag.hoverIndex, GridSlot.Target)
        } else {
            lifted
        }
        return (0 until capacity).map { offset -> placed.getOrNull(visual.start + offset) ?: GridSlot.Empty }
    }
}
