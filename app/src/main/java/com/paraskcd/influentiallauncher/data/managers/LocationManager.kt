package com.paraskcd.influentiallauncher.data.managers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.paraskcd.influentiallauncher.data.types.LocationWithName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationWithName(): Result<LocationWithName> {
        return suspendCancellableCoroutine { continuation ->
            if (!hasLocationPermission()) {
                continuation.resume(Result.failure(SecurityException("Location permission not granted")), onCancellation = null)
                return@suspendCancellableCoroutine
            }

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        getCityName(location) { cityName ->
                            continuation.resume(Result.success(LocationWithName(location, cityName)), onCancellation = null)
                        }
                    } else {
                        requestLocationUpdateWithName(continuation)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception), onCancellation = null)
                }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<Location> {
        val result = getCurrentLocationWithName()
        return result.map { it.location }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdateWithName(continuation: CancellableContinuation<Result<LocationWithName>>) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMaxUpdates(1)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    getCityName(location) { cityName ->
                        continuation.resume(Result.success(LocationWithName(location, cityName)), onCancellation = null)
                    }
                } else {
                    continuation.resume(Result.failure(Exception("Unable to get location")), onCancellation = null)
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun getCityName(location: Location, callback: (String?) -> Unit) {
        try {
            if (Geocoder.isPresent()) {
                val addresses: List<Address> = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                ) ?: emptyList()

                val cityName = addresses.firstOrNull()?.let { address ->
                    address.locality ?: address.subAdminArea ?: address.adminArea
                }
                callback(cityName)
            } else {
                callback(null)
            }
        } catch (e: Exception) {
            callback(null)
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}