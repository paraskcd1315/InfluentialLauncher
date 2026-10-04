// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.timetracking.BuildConfig
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.trackingStore: DataStore<Preferences> by preferencesDataStore(name = "timetracking")

@Singleton
class DataStoreCredentialsStore @Inject constructor(
    @ApplicationContext private val context: Context
) : CredentialsStore {

    override val credentials: Flow<TrackerCredentials> = context.trackingStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading credentials failed", error)
            emit(emptyPreferences())
        }
        .map { it.toCredentials() }
        .distinctUntilChanged()

    override suspend fun update(transform: (TrackerCredentials) -> TrackerCredentials) {
        context.trackingStore.edit { preferences ->
            preferences[WorkSchedule] = transform(preferences.toCredentials()).workSchedule
        }
    }

    private fun Preferences.toCredentials() = TrackerCredentials(
        togglToken = BuildConfig.TOGGL_API_TOKEN,
        kimaiUrl = BuildConfig.KIMAI_URL,
        kimaiToken = BuildConfig.KIMAI_TOKEN,
        workSchedule = this[WorkSchedule].orEmpty()
    )

    private companion object {
        const val LogTag = "CredentialsStore"
        val WorkSchedule = stringPreferencesKey("work_schedule")
    }
}
