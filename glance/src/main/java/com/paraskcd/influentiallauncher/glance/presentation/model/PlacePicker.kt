// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.model

import com.paraskcd.influentiallauncher.weather.domain.model.Place

data class PlacePicker(
    val adding: Boolean = false,
    val query: String = "",
    val results: List<Place> = emptyList(),
    val searching: Boolean = false
)
