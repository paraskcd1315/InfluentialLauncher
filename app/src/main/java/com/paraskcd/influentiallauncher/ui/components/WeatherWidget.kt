package com.paraskcd.influentiallauncher.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.microsoft.fluent.mobile.icons.R
import com.paraskcd.influentiallauncher.data.types.WeatherResponse
import com.paraskcd.influentiallauncher.data.types.WeatherState
import com.paraskcd.influentiallauncher.viewmodels.WeatherViewModel
import kotlin.text.get
import kotlin.text.toInt

@Composable
fun WeatherWidget(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val weatherState by weatherViewModel.weatherState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            weatherViewModel.checkPermissionAndLoadWeather()
        }
    }

    val refreshAndOpenExternal: () -> Unit = remember(weatherViewModel, context) {
        {
            weatherViewModel.refreshWeather()
            runCatching {
                val intent = context.packageManager
                    .getLaunchIntentForPackage("com.google.android.apps.weather")
                if (intent != null) {
                    context.startActivity(intent)
                }
            }
        }
    }

    when (val state = weatherState) { // Crear variable local para smart cast
        is WeatherState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is WeatherState.Success -> {
            val weather = state.weather
            val cityName = state.cityName
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .height(148.dp),
                color = Color.Transparent,
                onClick = refreshAndOpenExternal
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(getWeatherIcon(weather.current.weather_code)),
                            contentDescription = getWeatherDescription(weather.current.weather_code),
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            cityName?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = getWeatherDescription(weather.current.weather_code),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    Column {
                        Text(
                            text = "${weather.current.temperature_2m.toInt()}°C",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 64.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        is WeatherState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error: ${state.message}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { weatherViewModel.refreshWeather() }) {
                    Text("Retry")
                }
            }
        }
        is WeatherState.PermissionRequired -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Location permission required",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

private fun getWeatherDescription(weatherCode: Int): String {
    return when (weatherCode) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        95 -> "Thunderstorm"
        else -> "Unknown"
    }
}

private fun getWeatherIcon(weatherCode: Int): Int {
    return when (weatherCode) {
        0 -> R.drawable.ic_fluent_weather_sunny_24_regular
        1, 2, 3 -> R.drawable.ic_fluent_weather_partly_cloudy_day_24_regular
        45, 48 -> R.drawable.ic_fluent_weather_fog_24_regular
        51, 53, 55 -> R.drawable.ic_fluent_weather_drizzle_24_regular
        61, 63, 65 -> R.drawable.ic_fluent_weather_rain_24_regular
        71, 73, 75 -> R.drawable.ic_fluent_weather_snow_24_regular
        80, 81, 82 -> R.drawable.ic_fluent_weather_rain_showers_day_24_regular
        95 -> R.drawable.ic_fluent_weather_thunderstorm_24_regular
        else -> R.drawable.ic_fluent_weather_cloudy_24_regular
    }
}