// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack
import com.paraskcd.influentiallauncher.apps.domain.ports.IconPacks
import com.paraskcd.influentiallauncher.apps.domain.ports.IconStyleStore
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
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
class StartMenuViewModel @Inject constructor(
    private val settingsStore: SettingsStore,
    private val iconPacks: IconPacks,
    private val iconStyleStore: IconStyleStore
) : ViewModel() {

    val iconPack: StateFlow<String?> = iconStyleStore.style
        .map { it.iconPack }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(StopTimeoutMs), iconStyleStore.style.value.iconPack)

    private val _packs = MutableStateFlow<List<IconPack>?>(null)
    val packs: StateFlow<List<IconPack>?> = _packs.asStateFlow()

    fun loadPacks() {
        viewModelScope.launch { _packs.value = iconPacks.installed() }
    }

    fun setIconPack(packageName: String?) {
        viewModelScope.launch { iconStyleStore.setIconPack(packageName) }
    }

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
