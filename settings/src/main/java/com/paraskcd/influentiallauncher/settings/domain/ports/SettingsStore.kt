package com.paraskcd.influentiallauncher.settings.domain.ports

import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import kotlinx.coroutines.flow.Flow

interface SettingsStore {
    val settings: Flow<LauncherSettings>

    suspend fun update(transform: (LauncherSettings) -> LauncherSettings)
}
