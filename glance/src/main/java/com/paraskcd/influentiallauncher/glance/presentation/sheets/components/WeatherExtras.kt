package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.CloudRain
import com.composables.icons.lucide.Leaf
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sun
import com.composables.icons.lucide.Sunrise
import com.composables.icons.lucide.Sunset
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.AirQuality
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val SunFormat = DateTimeFormatter.ofPattern("HH:mm")

private data class Extra(val icon: ImageVector, val label: String, val value: String?, val detail: String? = null)

@Composable
fun WeatherExtras(forecast: Forecast, airQuality: AirQuality?, modifier: Modifier = Modifier) {
    val extras = buildList {
        forecast.uvIndex?.let {
            add(Extra(Lucide.Sun, stringResource(R.string.weather_uv), it.toString(), stringResource(WeatherVisuals.uvLabelOf(it))))
        }
        airQuality?.let { air ->
            val particles = listOfNotNull(
                air.pm25?.let { stringResource(R.string.weather_pm25, it.roundToInt()) },
                air.pm10?.let { stringResource(R.string.weather_pm10, it.roundToInt()) }
            ).joinToString(" · ")
            val detail = listOf(stringResource(WeatherVisuals.airLabelOf(air.europeanAqi)), particles).filter { it.isNotBlank() }.joinToString(" · ")
            add(Extra(Lucide.Leaf, stringResource(R.string.weather_air), air.europeanAqi.toString(), detail))
        }
        forecast.sunrise?.let { add(Extra(Lucide.Sunrise, stringResource(R.string.weather_sunrise), it.format(SunFormat))) }
        forecast.sunset?.let { add(Extra(Lucide.Sunset, stringResource(R.string.weather_sunset), it.format(SunFormat))) }
        forecast.rainTodayMm?.let { add(Extra(Lucide.CloudRain, stringResource(R.string.weather_rain_today), stringResource(R.string.weather_mm, it))) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s3), modifier = modifier.fillMaxWidth()) {
        extras.chunked(WeatherSheetMetrics.tileColumns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3), modifier = Modifier.height(IntrinsicSize.Min)) {
                row.forEach { extra ->
                    WeatherTile(
                        icon = extra.icon,
                        label = extra.label,
                        value = extra.value,
                        detail = extra.detail,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                repeat(WeatherSheetMetrics.tileColumns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
