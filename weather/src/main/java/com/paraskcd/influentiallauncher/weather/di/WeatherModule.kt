// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.di

import com.paraskcd.influentiallauncher.weather.BuildConfig
import com.paraskcd.influentiallauncher.weather.domain.ports.AirQualityProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.SavedPlaces
import com.paraskcd.influentiallauncher.weather.domain.ports.WarningProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.PlaceSearch
import com.paraskcd.influentiallauncher.weather.infrastructure.places.DataStoreSavedPlaces
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoPlaceSearch
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.ChainedWeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.aemet.AemetProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.meteoalarm.MeteoalarmWarnings
import com.paraskcd.influentiallauncher.weather.infrastructure.meteocat.MeteocatProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoAirQuality
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherModule {
    @Binds
    abstract fun bindWeatherSource(impl: ChainedWeatherSource): WeatherSource

    @Binds
    abstract fun bindAirQuality(impl: OpenMeteoAirQuality): AirQualityProvider

    @Binds
    abstract fun bindWarnings(impl: MeteoalarmWarnings): WarningProvider

    @Binds
    abstract fun bindSavedPlaces(impl: DataStoreSavedPlaces): SavedPlaces

    @Binds
    abstract fun bindPlaceSearch(impl: OpenMeteoPlaceSearch): PlaceSearch

    companion object {
        @Provides
        fun provideProviders(aemet: AemetProvider, meteocat: MeteocatProvider, openMeteo: OpenMeteoProvider): List<@JvmSuppressWildcards WeatherProvider> =
            if (BuildConfig.PERSONAL_EDITION) listOf(aemet, meteocat, openMeteo) else listOf(openMeteo)
    }
}
