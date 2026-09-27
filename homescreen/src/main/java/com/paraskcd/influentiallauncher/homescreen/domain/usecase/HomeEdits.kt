package com.paraskcd.influentiallauncher.homescreen.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomePage

object HomeEdits {
    fun normalize(layout: HomeLayout, newId: () -> String): HomeLayout {
        val pages = layout.pages.ifEmpty { listOf(HomePage(newId(), emptyList())) }
        val home = layout.homePageId.takeIf { id -> pages.any { it.id == id } } ?: pages.first().id
        return HomeLayout(pages, home)
    }

    fun addApp(layout: HomeLayout, app: AppId, newId: () -> String): HomeLayout {
        if (layout.pages.any { app in it.apps }) return layout
        val home = layout.pages.getOrNull(layout.homeIndex)
        val target = home?.takeIf { it.apps.size < HomeLayout.PageCapacity }
            ?: layout.pages.firstOrNull { it.apps.size < HomeLayout.PageCapacity }
        if (target == null) return layout.copy(pages = layout.pages + HomePage(newId(), listOf(app)))
        return layout.copy(pages = layout.pages.map { if (it.id == target.id) it.copy(apps = it.apps + app) else it })
    }

    fun move(layout: HomeLayout, app: AppId, toPageId: String, toIndex: Int, newId: () -> String): HomeLayout {
        if (layout.pages.none { it.id == toPageId }) return layout
        val without = layout.pages.map { page -> page.copy(apps = page.apps - app) }
        val inserted = without.map { page ->
            if (page.id != toPageId) page
            else page.copy(apps = page.apps.toMutableList().apply { add(toIndex.coerceIn(0, size), app) })
        }
        return layout.copy(pages = spill(inserted, newId))
    }

    fun removeApp(layout: HomeLayout, app: AppId): HomeLayout =
        layout.copy(pages = layout.pages.map { page -> page.copy(apps = page.apps - app) })

    fun addPage(layout: HomeLayout, id: String): HomeLayout =
        layout.copy(pages = layout.pages + HomePage(id, emptyList()))

    fun deletePage(layout: HomeLayout, pageId: String, newId: () -> String): HomeLayout =
        normalize(layout.copy(pages = layout.pages.filterNot { it.id == pageId }), newId)

    fun setHome(layout: HomeLayout, pageId: String): HomeLayout =
        if (layout.pages.any { it.id == pageId }) layout.copy(homePageId = pageId) else layout

    private fun spill(pages: List<HomePage>, newId: () -> String): List<HomePage> {
        val result = mutableListOf<HomePage>()
        var carry = emptyList<AppId>()
        pages.forEach { page ->
            val apps = carry + page.apps
            result += page.copy(apps = apps.take(HomeLayout.PageCapacity))
            carry = apps.drop(HomeLayout.PageCapacity)
        }
        while (carry.isNotEmpty()) {
            result += HomePage(newId(), carry.take(HomeLayout.PageCapacity))
            carry = carry.drop(HomeLayout.PageCapacity)
        }
        return result
    }
}
