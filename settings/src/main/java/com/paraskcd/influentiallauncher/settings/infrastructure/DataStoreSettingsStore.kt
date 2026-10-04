// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.settings.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class DataStoreSettingsStore @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsStore {

    override val settings: Flow<LauncherSettings> = context.settingsStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading settings failed", error)
            emit(emptyPreferences())
        }
        .map { it.toSettings() }

    override suspend fun update(transform: (LauncherSettings) -> LauncherSettings) {
        context.settingsStore.edit { preferences ->
            val next = transform(preferences.toSettings())
            preferences[ShowApps] = next.showAppsTab
            preferences[ShowCalendar] = next.showCalendarTab
            preferences[ShowContacts] = next.showContactsTab
            preferences[ShowLabels] = next.showLabels
            preferences[ShowStartLabels] = next.showStartLabels
            preferences[ShowClock] = next.showClock
            preferences[ShowGlance] = next.showGlance
            preferences[ShowStartButton] = next.showStartButton
            preferences[TintPanels] = next.tintPanels
            preferences[ShowRunningDots] = next.showRunningDots
            preferences[ShowBadges] = next.showBadges
        }
    }

    private fun Preferences.toSettings(): LauncherSettings {
        val defaults = LauncherSettings()
        return LauncherSettings(
            showAppsTab = this[ShowApps] ?: defaults.showAppsTab,
            showCalendarTab = this[ShowCalendar] ?: defaults.showCalendarTab,
            showContactsTab = this[ShowContacts] ?: defaults.showContactsTab,
            showLabels = this[ShowLabels] ?: defaults.showLabels,
            showStartLabels = this[ShowStartLabels] ?: defaults.showStartLabels,
            showClock = this[ShowClock] ?: defaults.showClock,
            showGlance = this[ShowGlance] ?: defaults.showGlance,
            showStartButton = this[ShowStartButton] ?: defaults.showStartButton,
            tintPanels = this[TintPanels] ?: defaults.tintPanels,
            showRunningDots = this[ShowRunningDots] ?: defaults.showRunningDots,
            showBadges = this[ShowBadges] ?: defaults.showBadges
        )
    }

    private companion object {
        const val LogTag = "SettingsStore"
        val ShowApps = booleanPreferencesKey("show_apps_tab")
        val ShowCalendar = booleanPreferencesKey("show_calendar_tab")
        val ShowContacts = booleanPreferencesKey("show_contacts_tab")
        val ShowLabels = booleanPreferencesKey("show_labels")
        val ShowStartLabels = booleanPreferencesKey("show_start_labels")
        val ShowClock = booleanPreferencesKey("show_clock")
        val ShowGlance = booleanPreferencesKey("show_glance")
        val ShowStartButton = booleanPreferencesKey("show_start_button")
        val TintPanels = booleanPreferencesKey("tint_panels")
        val ShowRunningDots = booleanPreferencesKey("show_running_dots")
        val ShowBadges = booleanPreferencesKey("show_badges")
    }
}
