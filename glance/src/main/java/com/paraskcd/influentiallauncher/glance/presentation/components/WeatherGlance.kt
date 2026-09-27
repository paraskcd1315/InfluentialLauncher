package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Cloud
import com.composables.icons.lucide.CloudDrizzle
import com.composables.icons.lucide.CloudFog
import com.composables.icons.lucide.CloudLightning
import com.composables.icons.lucide.CloudMoon
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.CloudSnow
import com.composables.icons.lucide.CloudSun
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Moon
import com.composables.icons.lucide.Sun
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition

@Composable
fun WeatherGlance(weather: Weather, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = iconOf(weather.condition, weather.isDay),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(GlanceMetrics.icon)
        )
        GlanceText(text = stringResource(R.string.glance_temperature, weather.temperatureC), style = MaterialTheme.typography.headlineMedium)
        Column(modifier = Modifier.weight(1f)) {
            GlanceText(text = stringResource(labelOf(weather.condition)), style = MaterialTheme.typography.titleSmall)
            weather.place?.let {
                GlanceText(text = it, style = MaterialTheme.typography.bodyMedium, alpha = GlanceMetrics.secondaryAlpha)
            }
        }
    }
}

private fun iconOf(condition: WeatherCondition, isDay: Boolean): ImageVector = when (condition) {
    WeatherCondition.Clear -> if (isDay) Lucide.Sun else Lucide.Moon
    WeatherCondition.PartlyCloudy -> if (isDay) Lucide.CloudSun else Lucide.CloudMoon
    WeatherCondition.Cloudy -> Lucide.Cloud
    WeatherCondition.Fog -> Lucide.CloudFog
    WeatherCondition.Drizzle -> Lucide.CloudDrizzle
    WeatherCondition.Rain -> Lucide.CloudRain
    WeatherCondition.Snow -> Lucide.CloudSnow
    WeatherCondition.Thunder -> Lucide.CloudLightning
}

@StringRes
private fun labelOf(condition: WeatherCondition): Int = when (condition) {
    WeatherCondition.Clear -> R.string.glance_clear
    WeatherCondition.PartlyCloudy -> R.string.glance_partly_cloudy
    WeatherCondition.Cloudy -> R.string.glance_cloudy
    WeatherCondition.Fog -> R.string.glance_fog
    WeatherCondition.Drizzle -> R.string.glance_drizzle
    WeatherCondition.Rain -> R.string.glance_rain
    WeatherCondition.Snow -> R.string.glance_snow
    WeatherCondition.Thunder -> R.string.glance_thunder
}
