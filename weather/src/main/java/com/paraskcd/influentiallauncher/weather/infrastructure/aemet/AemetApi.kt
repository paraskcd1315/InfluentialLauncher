// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

object AemetApi {
    const val BaseUrl = "https://opendata.aemet.es/opendata/api"
    const val ApiKeyHeader = "api_key"
    const val DataField = "datos"

    object Paths {
        const val Municipalities = "/maestro/municipios"
        const val HourlyForecast = "/prediccion/especifica/municipio/horaria/%s"
        const val DailyForecast = "/prediccion/especifica/municipio/diaria/%s"
    }

    object Fields {
        const val Id = "id"
        const val Name = "nombre"
        const val Latitude = "latitud_dec"
        const val Longitude = "longitud_dec"
        const val Prediction = "prediccion"
        const val Day = "dia"
        const val Date = "fecha"
        const val Period = "periodo"
        const val Value = "value"
        const val Sky = "estadoCielo"
        const val Temperature = "temperatura"
        const val FeelsLike = "sensTermica"
        const val Humidity = "humedadRelativa"
        const val Rain = "precipitacion"
        const val RainChance = "probPrecipitacion"
        const val WindAndGust = "vientoAndRachaMax"
        const val Speed = "velocidad"
        const val Sunrise = "orto"
        const val Sunset = "ocaso"
        const val Maximum = "maxima"
        const val Minimum = "minima"
        const val UvMax = "uvMax"
    }
}
