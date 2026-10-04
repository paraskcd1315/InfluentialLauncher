// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo

object OpenMeteoApi {
    const val ForecastBaseUrl = "https://api.open-meteo.com/v1"
    const val AirQualityBaseUrl = "https://air-quality-api.open-meteo.com/v1"
    const val GeocodingBaseUrl = "https://geocoding-api.open-meteo.com/v1"
    const val AutoTimezone = "auto"
    const val JsonFormat = "json"

    object Paths {
        const val Forecast = "/forecast"
        const val AirQuality = "/air-quality"
        const val Search = "/search"
    }

    object Geocoding {
        const val Name = "name"
        const val Count = "count"
        const val Language = "language"
        const val Format = "format"
        const val Results = "results"
        const val Latitude = "latitude"
        const val Longitude = "longitude"
        const val Region = "admin1"
        const val Province = "admin2"
        const val CountryCode = "country_code"
    }

    object Query {
        const val Latitude = "latitude"
        const val Longitude = "longitude"
        const val Current = "current"
        const val Hourly = "hourly"
        const val Daily = "daily"
        const val ForecastDays = "forecast_days"
        const val Timezone = "timezone"
    }

    object Fields {
        const val Time = "time"
        const val Temperature = "temperature_2m"
        const val FeelsLike = "apparent_temperature"
        const val Humidity = "relative_humidity_2m"
        const val Wind = "wind_speed_10m"
        const val WeatherCode = "weather_code"
        const val IsDay = "is_day"
        const val RainChance = "precipitation_probability"
        const val MaxTemperature = "temperature_2m_max"
        const val MinTemperature = "temperature_2m_min"
        const val MaxRainChance = "precipitation_probability_max"
        const val RainSum = "precipitation_sum"
        const val UvMax = "uv_index_max"
        const val Sunrise = "sunrise"
        const val Sunset = "sunset"
        const val WindDirection = "wind_direction_10m"
        const val MaxFeelsLike = "apparent_temperature_max"
        const val MinFeelsLike = "apparent_temperature_min"
        const val MaxHumidity = "relative_humidity_2m_max"
        const val MinHumidity = "relative_humidity_2m_min"
        const val MaxWind = "wind_speed_10m_max"
        const val MaxGust = "wind_gusts_10m_max"
        const val DominantWindDirection = "wind_direction_10m_dominant"
        const val EuropeanAqi = "european_aqi"
        const val Pm25 = "pm2_5"
        const val Pm10 = "pm10"
    }
}
