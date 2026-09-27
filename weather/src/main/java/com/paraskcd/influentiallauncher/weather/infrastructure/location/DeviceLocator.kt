package com.paraskcd.influentiallauncher.weather.infrastructure.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class DeviceLocator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val locations = context.getSystemService(LocationManager::class.java)

    suspend fun place(): Place? {
        val location = location() ?: return null
        val address = withTimeoutOrNull(TimeoutMs) {
            suspendCancellableCoroutine { continuation ->
                runCatching {
                    Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1) { results ->
                        continuation.resume(results.firstOrNull())
                    }
                }.onFailure { continuation.resume(null) }
            }
        }
        return Place(
            latitude = location.latitude,
            longitude = location.longitude,
            locality = address?.locality ?: address?.subAdminArea,
            region = address?.adminArea,
            countryCode = address?.countryCode
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun location(): Location? {
        val lastKnown = locations.getProviders(true)
            .mapNotNull { provider -> runCatching { locations.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull { it.time }
        if (lastKnown != null && System.currentTimeMillis() - lastKnown.time < FreshLocationMs) return lastKnown
        val provider = if (locations.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) LocationManager.NETWORK_PROVIDER else LocationManager.FUSED_PROVIDER
        val fresh = withTimeoutOrNull(TimeoutMs) {
            suspendCancellableCoroutine { continuation ->
                runCatching {
                    locations.getCurrentLocation(provider, null, ContextCompat.getMainExecutor(context)) { continuation.resume(it) }
                }.onFailure { continuation.resume(null) }
            }
        }
        return fresh ?: lastKnown
    }

    private companion object {
        const val FreshLocationMs = 60 * 60 * 1000L
        const val TimeoutMs = 10_000L
    }
}
