package com.paraskcd.influentiallauncher.weather.infrastructure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.weather.domain.model.AirQuality
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherReport
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherWarning
import com.paraskcd.influentiallauncher.weather.domain.ports.AirQualityProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WarningProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.location.DeviceLocator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChainedWeatherSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locator: DeviceLocator,
    private val providers: List<@JvmSuppressWildcards WeatherProvider>,
    private val airQuality: AirQualityProvider,
    private val warnings: WarningProvider
) : WeatherSource {

    override val permission: String = Manifest.permission.ACCESS_COARSE_LOCATION

    private val placeLock = Mutex()
    private val forecastLock = Mutex()
    private val extrasLock = Mutex()
    private var place: Cached<Place>? = null
    private val forecasts = mutableMapOf<WeatherSourceName, Cached<Forecast>>()
    private var extras: Cached<Extras>? = null

    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override suspend fun forecast(force: Boolean): Forecast? {
        val here = place(force) ?: return null
        return firstForecast(here, available(here), force)
    }

    override suspend fun report(source: WeatherSourceName?, force: Boolean): WeatherReport? = coroutineScope {
        val here = place(force) ?: return@coroutineScope null
        val sources = available(here)
        val extras = async { extras(here, force) }
        val chosen = sources.firstOrNull { it.name == source }
        val forecast = if (chosen != null) forecastFrom(chosen, here, force) else firstForecast(here, sources, force)
        val found = extras.await()
        forecast?.let {
            WeatherReport(
                forecast = it,
                sources = sources.map { provider -> provider.name },
                airQuality = found.airQuality,
                warnings = found.warnings
            )
        }
    }

    private fun available(here: Place): List<WeatherProvider> = providers.filter { it.covers(here) }

    private suspend fun firstForecast(here: Place, sources: List<WeatherProvider>, force: Boolean): Forecast? {
        for (provider in sources) {
            forecastFrom(provider, here, force)?.let { return it }
        }
        return null
    }

    private suspend fun forecastFrom(provider: WeatherProvider, here: Place, force: Boolean): Forecast? = forecastLock.withLock {
        val cached = forecasts[provider.name]
        if (!force && cached?.fresh() == true) return@withLock cached.value
        val fetched = runCatching { provider.forecast(here) }
            .onFailure { Log.w(LogTag, "${provider.name} failed", it) }
            .getOrNull()
        if (fetched == null) return@withLock cached?.value
        Log.d(LogTag, "weather from ${provider.name}")
        forecasts[provider.name] = Cached(fetched)
        fetched
    }

    private suspend fun extras(here: Place, force: Boolean): Extras = extrasLock.withLock {
        extras?.let { if (!force && it.fresh()) return@withLock it.value }
        coroutineScope {
            val air = async {
                runCatching { airQuality.airQuality(here) }.onFailure { Log.w(LogTag, "air quality failed", it) }.getOrNull()
            }
            val alerts = async {
                if (!warnings.covers(here)) emptyList()
                else runCatching { warnings.warnings(here) }.onFailure { Log.w(LogTag, "warnings failed", it) }.getOrDefault(emptyList())
            }
            Extras(air.await(), alerts.await()).also { extras = Cached(it) }
        }
    }

    private suspend fun place(force: Boolean): Place? = placeLock.withLock {
        place?.let { if (!force && it.fresh()) return@withLock it.value }
        if (!hasPermission()) return@withLock null
        val found = locator.place() ?: return@withLock place?.value
        place = Cached(found)
        found
    }

    private data class Extras(val airQuality: AirQuality?, val warnings: List<WeatherWarning>)

    private class Cached<T>(val value: T, private val at: Long = System.currentTimeMillis()) {
        fun fresh(): Boolean = System.currentTimeMillis() - at < CacheMs
    }

    private companion object {
        const val LogTag = "WeatherSource"
        const val CacheMs = 30 * 60 * 1000L
    }
}
