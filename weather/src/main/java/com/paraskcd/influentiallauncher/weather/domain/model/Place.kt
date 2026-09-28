// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.model

import java.util.Locale

data class Place(
    val latitude: Double,
    val longitude: Double,
    val locality: String?,
    val province: String?,
    val region: String?,
    val countryCode: String?
) {
    fun key(): String = String.format(Locale.ROOT, KeyFormat, latitude, longitude)

    private companion object {
        const val KeyFormat = "%.3f,%.3f"
    }
}
