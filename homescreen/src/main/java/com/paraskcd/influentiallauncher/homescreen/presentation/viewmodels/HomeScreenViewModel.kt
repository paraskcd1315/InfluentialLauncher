// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.AppSignalsSource
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.EditMode
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeDock
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreen
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreenState
import com.paraskcd.influentiallauncher.homescreen.presentation.state.VisualPage
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppActions
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppDataUsage
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val home: HomeScreen,
    private val dock: HomeDock,
    private val editMode: EditMode,
    private val pinnedApps: PinnedApps,
    signalsSource: AppSignalsSource,
    private val openApps: OpenApps,
    private val appActions: AppActions,
    private val appDataUsage: AppDataUsage,
    settingsStore: SettingsStore
) : ViewModel() {

    val showLabels: StateFlow<Boolean> = settingsStore.settings
        .map { it.showLabels }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), true)

    val signals: StateFlow<AppSignals> = signalsSource.signals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), AppSignals.None)

    val openPackages: StateFlow<Set<String>> = openApps.taskCounts
        .map { it.keys }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptySet())

    val runningPackages: StateFlow<Set<String>> = openApps.running
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptySet())

    val state: StateFlow<HomeScreenState?> = home.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    val wiggling: StateFlow<Boolean> = editMode.active

    val taskbarIds: StateFlow<List<AppId>> = pinnedApps.pinnedIds(PinTarget.Taskbar)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptyList())

    private val _overview = MutableStateFlow(false)
    val overview: StateFlow<Boolean> = _overview.asStateFlow()

    private val _menu = MutableStateFlow<LauncherApp?>(null)
    val menu: StateFlow<LauncherApp?> = _menu.asStateFlow()

    private val _removing = MutableStateFlow<LauncherApp?>(null)
    val removing: StateFlow<LauncherApp?> = _removing.asStateFlow()

    private val _deleting = MutableStateFlow<VisualPage?>(null)
    val deleting: StateFlow<VisualPage?> = _deleting.asStateFlow()

    private val _clearing = MutableStateFlow<LauncherApp?>(null)
    val clearing: StateFlow<LauncherApp?> = _clearing.asStateFlow()

    private val _dataApp = MutableStateFlow<LauncherApp?>(null)
    val dataApp: StateFlow<LauncherApp?> = _dataApp.asStateFlow()

    fun startWiggle() {
        _overview.value = false
        editMode.start()
    }

    fun stopWiggle() = editMode.stop()

    fun openOverview() {
        editMode.stop()
        _overview.value = true
    }

    fun closeOverview() {
        _overview.value = false
    }

    fun openMenu(app: LauncherApp) {
        _menu.value = app
    }

    fun closeMenu() {
        _menu.value = null
    }

    fun launch(app: AppId, origin: LaunchOrigin?): Boolean = home.launch(app, origin)

    val iconStyle: StateFlow<IconStyle> = home.iconStyle

    suspend fun icon(app: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = home.icon(app, sizePx, tint, background)

    fun cachedIcon(app: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = home.cachedIcon(app, sizePx, tint, background)

    fun move(app: AppId, pageId: String, index: Int) = run { home.move(app, pageId, index) }

    fun moveToNewPage(app: AppId) = run {
        val id = home.addPage()
        home.move(app, id, 0)
    }

    fun fromTaskbar(app: AppId, pageId: String?, index: Int) = run { dock.toHome(app, pageId, index) }

    fun toggleTaskbar(app: AppId) = run { pinnedApps.toggle(PinTarget.Taskbar, app) }

    fun openInfo(app: AppId) {
        home.openInfo(app)
    }

    fun uninstall(app: AppId) {
        home.uninstall(app)
    }

    fun closeApp(app: AppId) {
        openApps.close(app.packageName)
    }

    fun forceStop(app: AppId) {
        viewModelScope.launch {
            appActions.forceStop(app.packageName)
            openApps.refresh()
        }
    }

    fun askClear(app: LauncherApp) {
        _menu.value = null
        _clearing.value = app
    }

    fun cancelClear() {
        _clearing.value = null
    }

    fun confirmClear(app: LauncherApp) {
        viewModelScope.launch { appActions.clearStorage(app.id.packageName) }
    }

    fun openData(app: LauncherApp) {
        _menu.value = null
        _dataApp.value = app
    }

    fun closeData() {
        _dataApp.value = null
    }

    suspend fun dataUsage(app: AppId): List<DayData> = appDataUsage.weekly(app.packageName)

    fun addPage() = run { home.addPage() }

    fun setHome(screen: VisualPage) = run { home.setHomeScreen(screen.page.id, screen.start) }

    fun askRemove(app: LauncherApp) {
        _removing.value = app
    }

    fun cancelRemove() {
        _removing.value = null
    }

    fun confirmRemove(app: LauncherApp) {
        _removing.value = null
        run { home.remove(app.id) }
    }

    fun askDelete(screen: VisualPage) {
        _deleting.value = screen
    }

    fun cancelDelete() {
        _deleting.value = null
    }

    fun confirmDelete(screen: VisualPage) {
        _deleting.value = null
        run { home.deleteScreen(screen.page.id, screen.start, screen.slots.size) }
    }

    private fun run(block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }.onFailure { Log.w(LogTag, "home edit failed", it) }
        }
    }

    private companion object {
        const val LogTag = "HomeScreen"
        const val StopTimeoutMs = 5_000L
    }
}
