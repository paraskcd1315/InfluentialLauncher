package com.paraskcd.influentiallauncher.homescreen.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomePage

object HomeEdits {
    fun normalize(layout: HomeLayout, newId: () -> String): HomeLayout {
        val seen = mutableSetOf<AppId>()
        val cleaned = layout.pages.map { page ->
            page.copy(apps = SlotPlacement.trimEnd(page.apps.map { app -> app?.takeIf { seen.add(it) } }))
        }
        val pages = cleaned.ifEmpty { listOf(HomePage(newId(), emptyList())) }
        val home = layout.homePageId.takeIf { id -> pages.any { it.id == id } } ?: pages.first().id
        return HomeLayout(pages, home)
    }

    fun addApp(layout: HomeLayout, app: AppId, newId: () -> String): HomeLayout {
        if (layout.pages.any { app in it.apps }) return layout
        val home = layout.pages.getOrNull(layout.homeIndex)
            ?: return layout.copy(pages = layout.pages + HomePage(newId(), listOf(app)))
        val gap = home.apps.indexOf(null).takeIf { it >= 0 } ?: home.apps.size
        return layout.copy(pages = layout.pages.map { if (it.id == home.id) it.copy(apps = SlotPlacement.place(it.apps, gap, app)) else it })
    }

    fun move(layout: HomeLayout, app: AppId, toPageId: String, toIndex: Int): HomeLayout {
        if (layout.pages.none { it.id == toPageId }) return layout
        val lifted = removeApp(layout, app, trim = false)
        return lifted.copy(
            pages = lifted.pages.map { page ->
                val placed = if (page.id == toPageId) SlotPlacement.place(page.apps, toIndex, app) else page.apps
                page.copy(apps = SlotPlacement.trimEnd(placed))
            }
        )
    }

    fun removeApp(layout: HomeLayout, app: AppId): HomeLayout = removeApp(layout, app, trim = true)

    fun addPage(layout: HomeLayout, id: String): HomeLayout =
        layout.copy(pages = layout.pages + HomePage(id, emptyList()))

    fun deletePage(layout: HomeLayout, pageId: String, newId: () -> String): HomeLayout =
        normalize(layout.copy(pages = layout.pages.filterNot { it.id == pageId }), newId)

    fun setHome(layout: HomeLayout, pageId: String): HomeLayout =
        if (layout.pages.any { it.id == pageId }) layout.copy(homePageId = pageId) else layout

    private fun removeApp(layout: HomeLayout, app: AppId, trim: Boolean): HomeLayout =
        layout.copy(
            pages = layout.pages.map { page ->
                val emptied = page.apps.map { if (it == app) null else it }
                page.copy(apps = if (trim) SlotPlacement.trimEnd(emptied) else emptied)
            }
        )
}
