// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.apps.domain.model.AppIconChoice
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.ports.IconStyleStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.iconStore: DataStore<Preferences> by preferencesDataStore(name = "icons")

@Singleton
class DataStoreIconStyleStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) : IconStyleStore {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val style: StateFlow<IconStyle> = context.iconStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading icon style failed", error)
            emit(emptyPreferences())
        }
        .map { it.toStyle() }
        .stateIn(scope, SharingStarted.Eagerly, IconStyle())

    override suspend fun setIconPack(packageName: String?) {
        context.iconStore.edit { preferences ->
            if (packageName == null) preferences.remove(IconPackKey) else preferences[IconPackKey] = packageName
        }
    }

    override suspend fun choose(id: AppId, choice: AppIconChoice?) {
        context.iconStore.edit { preferences ->
            val others = preferences[ChoicesKey].orEmpty().filterNot { it.substringBefore(Separator) == id.key }
            preferences[ChoicesKey] = if (choice == null) others.toSet() else others.toSet() + encode(id, choice)
        }
    }

    private fun Preferences.toStyle() = IconStyle(
        iconPack = this[IconPackKey],
        choices = this[ChoicesKey].orEmpty().mapNotNull(::decode).toMap()
    )

    private fun encode(id: AppId, choice: AppIconChoice): String = listOf(id.key, choice.iconPack, choice.drawable).joinToString(Separator)

    private fun decode(entry: String): Pair<AppId, AppIconChoice>? {
        val parts = entry.split(Separator)
        if (parts.size != EntryParts) return null
        val id = AppId.fromKey(parts[0]) ?: return null
        return id to AppIconChoice(parts[1], parts[2])
    }

    private companion object {
        const val LogTag = "IconStyleStore"
        const val Separator = "\t"
        const val EntryParts = 3
        val IconPackKey = stringPreferencesKey("icon_pack")
        val ChoicesKey = stringSetPreferencesKey("app_icons")
    }
}
