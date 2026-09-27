package com.paraskcd.influentiallauncher.weather.di

import com.paraskcd.influentiallauncher.weather.domain.ports.AirQualityProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WarningProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.ChainedWeatherSource
import com.paraskcd.influentiallauncher.weather.infrastructure.aemet.AemetProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.meteoalarm.MeteoalarmWarnings
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

    companion object {
        @Provides
        fun provideProviders(aemet: AemetProvider, openMeteo: OpenMeteoProvider): List<@JvmSuppressWildcards WeatherProvider> =
            listOf(aemet, openMeteo)
    }
}
