// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import com.paraskcd.influentiallauncher.homescreen.domain.model.AppSignals
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.AppSignalsSource
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.EditMode
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeDock
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.AppDragPayload
import com.paraskcd.influentiallauncher.homescreen.presentation.drag.DragSource
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import com.paraskcd.influentiallauncher.tasks.domain.model.DayData
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppActions
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppDataUsage
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskbarViewModel @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinnedApps: PinnedApps,
    private val dock: HomeDock,
    private val editMode: EditMode,
    signalsSource: AppSignalsSource,
    private val openApps: OpenApps,
    private val appActions: AppActions,
    private val appDataUsage: AppDataUsage,
    settingsStore: SettingsStore
) : ViewModel() {

    val showStart: StateFlow<Boolean> = settingsStore.settings
        .map { it.showStartButton }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), true)

    val signals: StateFlow<AppSignals> = signalsSource.signals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), AppSignals.None)

    val openPackages: StateFlow<Set<String>> = openApps.taskCounts
        .map { it.keys }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptySet())

    val runningPackages: StateFlow<Set<String>> = openApps.running
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptySet())

    val pinned: StateFlow<List<LauncherApp>?> = pinnedApps.pinned(PinTarget.Taskbar)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    val startPins: StateFlow<List<AppId>> = pinnedApps.pinnedIds(PinTarget.Start)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptyList())

    val wiggling: StateFlow<Boolean> = editMode.active

    fun launch(id: AppId, origin: LaunchOrigin?) {
        installedApps.launch(id, origin)
    }

    fun startEdit() {
        editMode.start()
    }

    fun drop(payload: AppDragPayload, index: Int) {
        viewModelScope.launch {
            when (payload.source) {
                DragSource.Taskbar -> dock.reorderTaskbar(payload.app.id, index)
                DragSource.Home -> dock.toTaskbar(payload.app.id, index)
            }
        }
    }

    fun togglePin(target: PinTarget, id: AppId) {
        viewModelScope.launch { pinnedApps.toggle(target, id) }
    }

    fun openInfo(id: AppId) {
        installedApps.openInfo(id, null)
    }

    fun uninstall(id: AppId) {
        installedApps.uninstall(id)
    }

    fun closeApp(id: AppId) {
        openApps.close(id.packageName)
    }

    fun forceStop(id: AppId) {
        viewModelScope.launch {
            appActions.forceStop(id.packageName)
            openApps.refresh()
        }
    }

    fun clearStorage(id: AppId) {
        viewModelScope.launch { appActions.clearStorage(id.packageName) }
    }

    suspend fun dataUsage(id: AppId): List<DayData> = appDataUsage.weekly(id.packageName)

    val iconStyle: StateFlow<IconStyle> = installedApps.iconStyle

    suspend fun icon(id: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = installedApps.icon(id, sizePx, tint, background)

    fun cachedIcon(id: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = installedApps.cachedIcon(id, sizePx, tint, background)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
