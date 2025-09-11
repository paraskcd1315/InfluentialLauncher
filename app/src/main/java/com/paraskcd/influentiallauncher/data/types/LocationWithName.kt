package com.paraskcd.influentiallauncher.data.types

import android.location.Location

data class LocationWithName(
    val location: Location,
    val cityName: String?
)
