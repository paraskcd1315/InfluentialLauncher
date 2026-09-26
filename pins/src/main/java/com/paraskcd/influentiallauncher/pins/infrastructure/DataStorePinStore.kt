package com.paraskcd.influentiallauncher.pins.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.pins.domain.model.PinTarget
import com.paraskcd.influentiallauncher.pins.domain.ports.PinStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.taskbarStore: DataStore<Preferences> by preferencesDataStore(name = "taskbar")

@Singleton
class DataStorePinStore @Inject constructor(
    @ApplicationContext private val context: Context
) : PinStore {

    override fun pins(target: PinTarget): Flow<List<AppId>> = context.taskbarStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading pins failed", error)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[keyOf(target)].orEmpty()
                .split(Separator)
                .mapNotNull { AppId.fromKey(it) }
        }

    override suspend fun save(target: PinTarget, pins: List<AppId>) {
        context.taskbarStore.edit { preferences ->
            preferences[keyOf(target)] = pins.joinToString(Separator) { it.key }
        }
    }

    private fun keyOf(target: PinTarget): Preferences.Key<String> = when (target) {
        PinTarget.Taskbar -> TaskbarKey
        PinTarget.Start -> StartKey
    }

    private companion object {
        const val LogTag = "PinStore"
        const val Separator = "\n"
        val TaskbarKey = stringPreferencesKey("pinned_apps")
        val StartKey = stringPreferencesKey("start_pinned_apps")
    }
}
