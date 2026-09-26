package com.paraskcd.influentiallauncher.contacts.infrastructure

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.contacts.domain.ports.ContactPinStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.contactsStore: DataStore<Preferences> by preferencesDataStore(name = "contacts")

@Singleton
class DataStoreContactPinStore @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactPinStore {

    override fun pins(): Flow<List<String>> = context.contactsStore.data
        .catch { error ->
            if (error !is IOException) throw error
            Log.w(LogTag, "reading contact pins failed", error)
            emit(emptyPreferences())
        }
        .map { preferences -> decode(preferences[PinsKey]) }

    override suspend fun toggle(lookupKey: String) {
        context.contactsStore.edit { preferences ->
            val current = decode(preferences[PinsKey])
            val next = if (lookupKey in current) current - lookupKey else current + lookupKey
            preferences[PinsKey] = next.joinToString(Separator)
        }
    }

    private fun decode(value: String?): List<String> =
        value.orEmpty().split(Separator).filter { it.isNotBlank() }

    private companion object {
        const val LogTag = "ContactPinStore"
        const val Separator = "\n"
        val PinsKey = stringPreferencesKey("pinned_contacts")
    }
}
