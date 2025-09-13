package com.paraskcd.influentiallauncher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.managers.LocationManager
import com.paraskcd.influentiallauncher.data.types.WeatherState
import com.paraskcd.influentiallauncher.services.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherService: WeatherService,
    private val locationManager: LocationManager
): ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)

    init {
        checkPermissionAndLoadWeather()
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