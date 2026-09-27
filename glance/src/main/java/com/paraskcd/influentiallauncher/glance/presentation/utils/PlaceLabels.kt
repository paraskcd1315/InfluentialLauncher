package com.paraskcd.influentiallauncher.glance.presentation.utils

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import java.util.Locale

object PlaceLabels {
    fun nameOf(place: Place): String =
        place.locality ?: place.province ?: String.format(Locale.ROOT, CoordinatesFormat, place.latitude, place.longitude)

    fun detailOf(place: Place): String? =
        listOfNotNull(place.province?.takeIf { it != place.locality }, place.region, place.countryCode)
            .distinct()
            .joinToString(", ")
            .ifBlank { null }

    private const val CoordinatesFormat = "%.2f, %.2f"
}
