package com.paraskcd.influentiallauncher.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.paraskcd.influentiallauncher.data.managers.LocationManager
import com.paraskcd.influentiallauncher.data.repositories.WeatherRepository
import com.paraskcd.influentiallauncher.data.types.WeatherState
import com.paraskcd.influentiallauncher.services.WeatherService
import com.paraskcd.influentiallauncher.utls.shouldAttemptWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val weatherService: WeatherService,
    private val locationManager: LocationManager,
    private val repository: WeatherRepository,
): ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    private var passiveLoopJob: Job? = null

    init {
        checkPermissionAndLoadWeather()
    }

    fun ensureFreshWeather(force: Boolean = false) {
        if (!context.shouldAttemptWeather()) return
        viewModelScope.launch {
            val (lat, lon) = getLastKnownCoordinates() ?: return@launch
            val res = repository.maybeFetch(lat, lon, force)
            if (res != null) {
                res.onSuccess { weather ->
                    // Mantener ciudad previa si ya se obtuvo antes
                    val currentCity = (weatherState.value as? WeatherState.Success)?.cityName
                    _weatherState.value = WeatherState.Success(weather, currentCity)
                }.onFailure { e ->
                    // Sólo sobreescribir si no hay un éxito previo
                    if (weatherState.value !is WeatherState.Success) {
                        _weatherState.value = WeatherState.Error(e.message ?: "Error obteniendo clima")
                    }
                }
            }
        }
    }

    fun startPassiveLoop(isLauncherVisible: Boolean) {
        if (!isLauncherVisible) {
            passiveLoopJob?.cancel()
            passiveLoopJob = null
            return
        }
        if (passiveLoopJob?.isActive == true) return
        passiveLoopJob = viewModelScope.launch {
            while (true) {
                ensureFreshWeather(force = false)
                // Esperar hasta el próximo earliest guardado
                val next = repository.getLastFetchEpoch() + 60 * 60 * 1000 // fallback si no se leyó jitter
                val wait = (next - System.currentTimeMillis()).coerceAtLeast(5_000L)
                delay(wait)
            }
        }
    }

    private suspend fun getLastKnownCoordinates(): Pair<Double, Double>? {
        // Asegurar permisos
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return null

        val fused = LocationServices.getFusedLocationProviderClient(context)

        // 1. Intentar caché rápida
        try {
            val cached = fused.lastLocation.await()
            if (cached != null) return cached.latitude to cached.longitude
        } catch (_: Exception) { /* ignorar y seguir */ }

        // 2. Solicitud activa (timeout para no colgarse)
        return try {
            withTimeout(5000L) {
                val loc = fused
                    .getCurrentLocation(
                        if (fine) Priority.PRIORITY_BALANCED_POWER_ACCURACY else Priority.PRIORITY_PASSIVE,
                        null
                    )
                    .await()
                loc?.let { it.latitude to it.longitude }
            }
        } catch (_: Exception) {
            null
        }
    }

    fun checkPermissionAndLoadWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading

            try {
                val location = locationManager.getCurrentLocationWithName()
                location.fold(
                    onSuccess = { locationWithName ->
                        _hasLocationPermission.value = true
                        loadWeather(locationWithName.location.latitude, locationWithName.location.longitude, locationWithName.cityName)
                    },
                    onFailure = { exception ->
                        when (exception) {
                            is SecurityException -> {
                                _hasLocationPermission.value = false
                                _weatherState.value = WeatherState.PermissionRequired
                            }
                            else -> {
                                _weatherState.value = WeatherState.Error(exception.message ?: "Error getting location")
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun loadWeather(latitude: Double, longitude: Double, cityName: String?) {
        val result = weatherService.getWeather(latitude, longitude)
        result.fold(
            onSuccess = { weather ->
                _weatherState.value = WeatherState.Success(weather, cityName)
            },
            onFailure = { exception ->
                _weatherState.value = WeatherState.Error(exception.message ?: "Error loading weather")
            }
        )
    }

    fun refreshWeather() {
        checkPermissionAndLoadWeather()
    }
}