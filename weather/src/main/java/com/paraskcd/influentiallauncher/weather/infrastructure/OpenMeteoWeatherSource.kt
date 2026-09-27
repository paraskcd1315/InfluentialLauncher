package com.paraskcd.influentiallauncher.weather.infrastructure

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.math.roundToInt

@Singleton
class OpenMeteoWeatherSource @Inject constructor(
    @ApplicationContext private val context: Context
) : WeatherSource {

    override val permission: String = Manifest.permission.ACCESS_COARSE_LOCATION

    private val lock = Mutex()
    private var cached: Pair<Long, Weather>? = null
    private val locations = context.getSystemService(LocationManager::class.java)

    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override suspend fun current(force: Boolean): Weather? = lock.withLock {
        val now = System.currentTimeMillis()
        cached?.let { (at, weather) -> if (!force && now - at < CacheMs) return@withLock weather }
        if (!hasPermission()) return@withLock null
        val location = location() ?: return@withLock cached?.second
        val weather = runCatching { fetch(location) }
            .onFailure { Log.w(LogTag, "weather fetch failed", it) }
            .getOrNull() ?: return@withLock cached?.second
        cached = now to weather
        weather
    }

    @SuppressLint("MissingPermission")
    private suspend fun location(): Location? {
        val lastKnown = locations.getProviders(true)
            .mapNotNull { provider -> runCatching { locations.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { it.time }
        if (lastKnown != null && System.currentTimeMillis() - lastKnown.time < FreshLocationMs) return lastKnown
        val provider = if (locations.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) LocationManager.NETWORK_PROVIDER else LocationManager.FUSED_PROVIDER
        val fresh = withTimeoutOrNull(LocationTimeoutMs) {
            suspendCancellableCoroutine { continuation ->
                runCatching {
                    locations.getCurrentLocation(provider, null, ContextCompat.getMainExecutor(context)) { continuation.resume(it) }
                }.onFailure { continuation.resume(null) }
            }
        }
        return fresh ?: lastKnown
    }

    private suspend fun fetch(location: Location): Weather = withContext(Dispatchers.IO) {
        val url = String.format(Locale.US, ForecastUrl, location.latitude, location.longitude)
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = TimeoutMs
            readTimeout = TimeoutMs
        }
        val body = try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
        val current = JSONObject(body).getJSONObject("current")
        Weather(
            temperatureC = current.getDouble("temperature_2m").roundToInt(),
            condition = WeatherCondition.fromWmoCode(current.getInt("weather_code")),
            isDay = current.optInt("is_day", 1) == 1,
            place = place(location)
        )
    }

    private suspend fun place(location: Location): String? = withTimeoutOrNull(LocationTimeoutMs) {
        suspendCancellableCoroutine { continuation ->
            runCatching {
                Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1) { results ->
                    val address = results.firstOrNull()
                    continuation.resume(address?.locality ?: address?.subAdminArea ?: address?.adminArea)
                }
            }.onFailure { continuation.resume(null) }
        }
    }

    private companion object {
        const val LogTag = "WeatherSource"
        const val ForecastUrl = "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,weather_code,is_day&timezone=auto"
        const val CacheMs = 30 * 60 * 1000L
        const val FreshLocationMs = 60 * 60 * 1000L
        const val LocationTimeoutMs = 10_000L
        const val TimeoutMs = 10_000
    }
}
