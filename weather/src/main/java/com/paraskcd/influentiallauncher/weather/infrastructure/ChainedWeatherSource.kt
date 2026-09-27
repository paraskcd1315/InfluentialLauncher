package com.paraskcd.influentiallauncher.weather.infrastructure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.location.DeviceLocator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChainedWeatherSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locator: DeviceLocator,
    private val providers: List<@JvmSuppressWildcards WeatherProvider>
) : WeatherSource {

    override val permission: String = Manifest.permission.ACCESS_COARSE_LOCATION

    private val lock = Mutex()
    private var cached: Pair<Long, Weather>? = null

    override fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    override suspend fun current(force: Boolean): Weather? = lock.withLock {
        val now = System.currentTimeMillis()
        cached?.let { (at, weather) -> if (!force && now - at < CacheMs) return@withLock weather }
        if (!hasPermission()) return@withLock null
        val place = locator.place() ?: return@withLock cached?.second
        var weather: Weather? = null
        for (provider in providers) {
            if (!provider.covers(place)) continue
            weather = runCatching { provider.current(place) }
                .onFailure { Log.w(LogTag, "${provider::class.simpleName} failed", it) }
                .getOrNull()
            if (weather != null) {
                Log.d(LogTag, "weather from ${provider::class.simpleName}")
                break
            }
        }
        if (weather == null) return@withLock cached?.second
        cached = now to weather
        weather
    }

    private companion object {
        const val LogTag = "WeatherSource"
        const val CacheMs = 30 * 60 * 1000L
    }
}
