package com.paraskcd.influentiallauncher.taskbar.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
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

    fun launch(id: AppId, sourceBounds: Rect?) {
        installedApps.launch(id, sourceBounds)
    }

    fun reorder(order: List<AppId>) {
        viewModelScope.launch { pinnedApps.reorder(PinTarget.Taskbar, order) }
    }

    suspend fun icon(id: AppId, sizePx: Int, tint: Int): Bitmap? = installedApps.icon(id, sizePx, tint)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
