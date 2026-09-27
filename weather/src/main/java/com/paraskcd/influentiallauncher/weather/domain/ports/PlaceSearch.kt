package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Place

interface PlaceSearch {
    suspend fun search(query: String): List<Place>
}
