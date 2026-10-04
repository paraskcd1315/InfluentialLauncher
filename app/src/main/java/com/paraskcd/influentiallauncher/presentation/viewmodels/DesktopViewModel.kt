// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DesktopViewModel @Inject constructor(
    settingsStore: SettingsStore,
    private val installedApps: InstalledApps
) : ViewModel() {

    val settings: StateFlow<LauncherSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, LauncherSettings())

    val iconStyle: StateFlow<IconStyle> = installedApps.iconStyle

    suspend fun iconLayers(id: AppId, sizePx: Int, tint: Int, background: Int): Pair<Bitmap, Bitmap>? =
        installedApps.iconLayers(id, sizePx, tint, background)?.let { it.plate to it.glyph }
}
