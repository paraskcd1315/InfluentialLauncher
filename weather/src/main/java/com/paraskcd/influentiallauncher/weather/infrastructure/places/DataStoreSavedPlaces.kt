// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.places

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.ports.SavedPlaces
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.placesStore: DataStore<Preferences> by preferencesDataStore(name = "weather_places")

@Singleton
class DataStoreSavedPlaces @Inject constructor(
    @ApplicationContext private val context: Context
) : SavedPlaces {

    private val data: Flow<Preferences> = context.placesStore.data.catch { error ->
        if (error !is IOException) throw error
        Log.w(LogTag, "reading places failed", error)
        emit(emptyPreferences())
    }

    override val places: Flow<List<Place>> = data.map { decode(it[PlacesKey]) }.distinctUntilChanged()

    override val selected: Flow<Place?> = data.map { preferences ->
        val key = preferences[SelectedKey] ?: return@map null
        decode(preferences[PlacesKey]).firstOrNull { it.key() == key }
    }.distinctUntilChanged()

    override suspend fun save(place: Place) {
        context.placesStore.edit { preferences ->
            val current = decode(preferences[PlacesKey])
            if (current.none { it.key() == place.key() }) preferences[PlacesKey] = encode(current + place)
        }
    }

    override suspend fun remove(place: Place) {
        context.placesStore.edit { preferences ->
            preferences[PlacesKey] = encode(decode(preferences[PlacesKey]).filterNot { it.key() == place.key() })
            if (preferences[SelectedKey] == place.key()) preferences.remove(SelectedKey)
        }
    }

    override suspend fun select(place: Place?) {
        context.placesStore.edit { preferences ->
            if (place == null) preferences.remove(SelectedKey) else preferences[SelectedKey] = place.key()
        }
    }

    private fun encode(places: List<Place>): String = JSONArray().apply {
        places.forEach { place ->
            put(
                JSONObject()
                    .put(Fields.Latitude, place.latitude)
                    .put(Fields.Longitude, place.longitude)
                    .put(Fields.Locality, place.locality)
                    .put(Fields.Province, place.province)
                    .put(Fields.Region, place.region)
                    .put(Fields.Country, place.countryCode)
            )
        }
    }.toString()

    private fun decode(text: String?): List<Place> {
        if (text.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(text)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                Place(
                    latitude = item.getDouble(Fields.Latitude),
                    longitude = item.getDouble(Fields.Longitude),
                    locality = item.optStringOrNull(Fields.Locality),
                    province = item.optStringOrNull(Fields.Province),
                    region = item.optStringOrNull(Fields.Region),
                    countryCode = item.optStringOrNull(Fields.Country)
                )
            }
        }.onFailure { Log.w(LogTag, "decoding places failed", it) }.getOrDefault(emptyList())
    }

    private fun JSONObject.optStringOrNull(name: String): String? = if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }

    private object Fields {
        const val Latitude = "lat"
        const val Longitude = "lon"
        const val Locality = "locality"
        const val Province = "province"
        const val Region = "region"
        const val Country = "country"
    }

    private companion object {
        const val LogTag = "SavedPlaces"
        val PlacesKey = stringPreferencesKey("places")
        val SelectedKey = stringPreferencesKey("selected")
    }
}
