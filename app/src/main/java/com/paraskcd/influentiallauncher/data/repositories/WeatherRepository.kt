package com.paraskcd.influentiallauncher.data.repositories

import android.content.Context
import com.paraskcd.influentiallauncher.data.types.WeatherResponse
import com.paraskcd.influentiallauncher.services.WeatherService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private val Context.weatherStore by preferencesDataStore("weather_meta")

class WeatherRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val service: WeatherService
) {
    private val KEY_LAST_FETCH = longPreferencesKey("last_fetch_epoch")
    private val KEY_NEXT_EARLIEST = longPreferencesKey("next_earliest_epoch")
    private val mutex = Mutex()

    private val minInterval: Duration = 1.hours          // Debounce duro
    private val jitterMax: Duration = 30.minutes         // Jitter aleatorio

    suspend fun maybeFetch(latitude: Double, longitude: Double, force: Boolean = false): Result<WeatherResponse>? {
        val now = System.currentTimeMillis()
        return mutex.withLock {
            val prefs = context.weatherStore.data.first()
            val last = prefs[KEY_LAST_FETCH] ?: 0L
            val nextEarliest = prefs[KEY_NEXT_EARLIEST] ?: 0L

            if (!force) {
                if (now < nextEarliest) return null // Aún dentro de ventana bloqueada
            }

            val result = service.getWeather(latitude, longitude)
            if (result.isSuccess) {
                val jitterMillis = (0 until jitterMax.inWholeMilliseconds).random()
                val next = now + minInterval.inWholeMilliseconds + jitterMillis
                context.weatherStore.edit {
                    it[KEY_LAST_FETCH] = now
                    it[KEY_NEXT_EARLIEST] = next
                }
            }
            return result
        }
    }

    suspend fun getLastFetchEpoch(): Long {
        val prefs = context.weatherStore.data.first()
        return prefs[KEY_LAST_FETCH] ?: 0L
    }
}