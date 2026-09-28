// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.Weather

@Composable
fun WeatherGlance(weather: Weather, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClickLabel = stringResource(R.string.weather_open), onClick = onOpen)
    ) {
        Icon(
            imageVector = WeatherVisuals.iconOf(weather.condition, weather.isDay),
            contentDescription = null,
            tint = LocalWallpaperInk.current.content,
            modifier = Modifier.size(GlanceMetrics.icon)
        )
        GlanceText(text = stringResource(R.string.glance_temperature, weather.temperatureC), style = MaterialTheme.typography.headlineMedium)
        Column(modifier = Modifier.weight(1f)) {
            GlanceText(text = stringResource(WeatherVisuals.labelOf(weather.condition)), style = MaterialTheme.typography.titleSmall)
            weather.place?.let {
                GlanceText(text = it, style = MaterialTheme.typography.bodyMedium, alpha = GlanceMetrics.secondaryAlpha)
            }
        }
    }
}
