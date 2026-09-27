package com.paraskcd.influentiallauncher.homescreen.domain.usecase

import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import com.paraskcd.influentiallauncher.homescreen.domain.ports.HomeStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class HomeScreenPage(val id: String, val apps: List<LauncherApp>)

data class HomeScreenState(val pages: List<HomeScreenPage>, val homeIndex: Int)

@Singleton
class HomeScreen @Inject constructor(
    private val installedApps: InstalledApps,
    private val store: HomeStore
) {
    val state: Flow<HomeScreenState> = combine(installedApps.apps, store.layout) { apps, layout ->
        val byId = apps.associateBy { it.id }
        HomeScreenState(
            pages = layout.pages.map { page -> HomeScreenPage(page.id, page.apps.mapNotNull { byId[it] }) },
            homeIndex = layout.homeIndex
        )
    }

    val layout: Flow<HomeLayout> = store.layout

    suspend fun add(app: AppId) = store.update { HomeEdits.addApp(it, app, ::newId) }

    suspend fun move(app: AppId, toPageId: String, toIndex: Int) = store.update { HomeEdits.move(it, app, toPageId, toIndex, ::newId) }

    suspend fun remove(app: AppId) = store.update { HomeEdits.removeApp(it, app) }

    suspend fun addPage(): String {
        val id = newId()
        store.update { HomeEdits.addPage(it, id) }
        return id
    }

    suspend fun deletePage(pageId: String) = store.update { HomeEdits.deletePage(it, pageId, ::newId) }

    suspend fun setHome(pageId: String) = store.update { HomeEdits.setHome(it, pageId) }

    fun launch(app: AppId, origin: LaunchOrigin?) = installedApps.launch(app, origin)

    suspend fun icon(app: AppId, sizePx: Int, tint: Int?) = installedApps.icon(app, sizePx, tint)

    private fun newId(): String = UUID.randomUUID().toString()
}
