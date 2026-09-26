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
        }
    }

    private fun Preferences.toSettings(): LauncherSettings {
        val defaults = LauncherSettings()
        return LauncherSettings(
            showAppsTab = this[ShowApps] ?: defaults.showAppsTab,
            showCalendarTab = this[ShowCalendar] ?: defaults.showCalendarTab,
            showContactsTab = this[ShowContacts] ?: defaults.showContactsTab
        )
    }

    private companion object {
        const val LogTag = "SettingsStore"
        val ShowApps = booleanPreferencesKey("show_apps_tab")
        val ShowCalendar = booleanPreferencesKey("show_calendar_tab")
        val ShowContacts = booleanPreferencesKey("show_contacts_tab")
    }
}
