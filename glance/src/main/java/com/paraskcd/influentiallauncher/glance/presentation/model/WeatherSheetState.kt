// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.weather.domain.model.WeatherReport
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName

data class WeatherSheetState(
    val report: WeatherReport? = null,
    val selected: WeatherSourceName? = null,
    val loading: Boolean = true
)
