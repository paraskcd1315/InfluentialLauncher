// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DesktopViewModel @Inject constructor(
    settingsStore: SettingsStore
) : ViewModel() {

    val settings: StateFlow<LauncherSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, LauncherSettings())
}
