// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartMenuViewModel @Inject constructor(
    private val settingsStore: SettingsStore
) : ViewModel() {

    val settings: StateFlow<LauncherSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), LauncherSettings())

    val tabs: StateFlow<List<StartMenuTab>> = settingsStore.settings
        .map { settings ->
            buildList {
                if (settings.showAppsTab) add(StartMenuTab.Apps)
                if (settings.showCalendarTab) add(StartMenuTab.Calendar)
                if (settings.showContactsTab) add(StartMenuTab.Contacts)
                add(StartMenuTab.Settings)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), StartMenuTab.entries)

    fun setTabShown(tab: StartMenuTab, shown: Boolean) {
        viewModelScope.launch {
            settingsStore.update { current ->
                when (tab) {
                    StartMenuTab.Apps -> current.copy(showAppsTab = shown)
                    StartMenuTab.Calendar -> current.copy(showCalendarTab = shown)
                    StartMenuTab.Contacts -> current.copy(showContactsTab = shown)
                    StartMenuTab.Settings -> current
                }
            }
        }
    }

    fun updateSettings(transform: (LauncherSettings) -> LauncherSettings) {
        viewModelScope.launch { settingsStore.update(transform) }
    }

    private companion object {
        const val StopTimeoutMs = 5_000L
    }
}
