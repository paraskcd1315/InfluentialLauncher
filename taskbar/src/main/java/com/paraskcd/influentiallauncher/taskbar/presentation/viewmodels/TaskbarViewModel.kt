package com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskbarViewModel @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinnedApps: PinnedApps
) : ViewModel() {

    val pinned: StateFlow<List<LauncherApp>?> = pinnedApps.pinned(PinTarget.Taskbar)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    val startPins: StateFlow<List<AppId>> = pinnedApps.pinnedIds(PinTarget.Start)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), emptyList())

    fun launch(id: AppId, origin: LaunchOrigin?) {
        installedApps.launch(id, origin)
    }

    fun reorder(order: List<AppId>) {
        viewModelScope.launch { pinnedApps.reorder(PinTarget.Taskbar, order) }
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

    suspend fun icon(id: AppId, sizePx: Int, tint: Int): Bitmap? = installedApps.icon(id, sizePx, tint)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
