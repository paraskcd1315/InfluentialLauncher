package com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import com.paraskcd.influentiallauncher.taskbar.domain.usecase.PinnedApps
import com.paraskcd.influentiallauncher.taskbar.presentation.model.PickerRow
import com.paraskcd.influentiallauncher.taskbar.presentation.model.StatusState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskbarViewModel @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinnedApps: PinnedApps,
    deviceStatus: DeviceStatusSource
) : ViewModel() {

    val pinned: StateFlow<List<LauncherApp>?> = pinnedApps.pinned
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    val pickerRows: StateFlow<List<PickerRow>?> = combine(installedApps.apps, pinnedApps.pinnedIds) { apps, pins ->
        apps.map { PickerRow(app = it, pinned = it.id in pins) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    val status: StateFlow<StatusState?> = combine(
        deviceStatus.battery,
        deviceStatus.wifi,
        deviceStatus.cellular
    ) { battery, wifi, cellular ->
        StatusState(battery, wifi, cellular)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun launch(id: AppId, sourceBounds: Rect?) {
        installedApps.launch(id, sourceBounds)
    }

    fun togglePin(id: AppId) {
        viewModelScope.launch { pinnedApps.toggle(id) }
    }

    fun reorder(order: List<AppId>) {
        viewModelScope.launch { pinnedApps.reorder(order) }
    }

    suspend fun icon(id: AppId, sizePx: Int): Bitmap? = installedApps.icon(id, sizePx)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
