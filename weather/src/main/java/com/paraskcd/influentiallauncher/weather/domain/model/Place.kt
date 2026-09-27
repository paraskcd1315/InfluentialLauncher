package com.paraskcd.influentiallauncher.weather.domain.model

data class Place(
    val latitude: Double,
    val longitude: Double,
    val locality: String?,
    val region: String?,
    val countryCode: String?
)
