// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.ports

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface SavedPlaces {
    val places: Flow<List<Place>>

    suspend fun save(place: Place)

    suspend fun remove(place: Place)
}
