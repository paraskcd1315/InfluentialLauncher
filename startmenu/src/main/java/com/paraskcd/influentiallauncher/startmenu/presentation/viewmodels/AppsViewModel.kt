// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.homescreen.domain.usecase.HomeScreen
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.usecase.PinnedApps
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuContent
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppSections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val installedApps: InstalledApps,
    private val pinnedApps: PinnedApps,
    private val homeScreen: HomeScreen
) : ViewModel() {

    fun addToHome(id: AppId) {
        viewModelScope.launch { homeScreen.add(id) }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val content: StateFlow<StartMenuContent?> = combine(
        installedApps.apps,
        pinnedApps.pinnedIds(PinTarget.Taskbar),
        pinnedApps.pinnedIds(PinTarget.Start),
        _query
    ) { apps, taskbar, start, query ->
        AppSections.of(apps, taskbar, start, query)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), null)

    fun setQuery(value: String) {
        _query.value = value
    }

    fun launch(id: AppId, origin: LaunchOrigin?) {
        installedApps.launch(id, origin)
    }

    fun togglePin(target: PinTarget, id: AppId) {
        viewModelScope.launch { pinnedApps.toggle(target, id) }
    }

    fun openInfo(id: AppId, sourceBounds: Rect?) {
        installedApps.openInfo(id, sourceBounds)
    }

    fun uninstall(id: AppId) {
        installedApps.uninstall(id)
    }

    suspend fun icon(id: AppId, sizePx: Int, tint: Int, background: Int): Bitmap? = installedApps.icon(id, sizePx, tint, background)

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
