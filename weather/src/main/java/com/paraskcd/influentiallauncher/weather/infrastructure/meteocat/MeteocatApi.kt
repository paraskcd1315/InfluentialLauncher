// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

object MeteocatApi {
    const val BaseUrl = "https://api.meteo.cat"
    const val ApiKeyHeader = "x-api-key"
    const val TemperatureVariable = 32

    object Paths {
        const val Municipalities = "/referencia/v1/municipis"
        const val Symbols = "/referencia/v1/simbols"
        const val DailyForecast = "/pronostic/v1/municipal/%s"
        const val HourlyForecast = "/pronostic/v1/municipalHoraria/%s"
        const val RepresentativeStations = "/xema/v1/representatives/metadades/municipis/%s/variables/%d"
        const val VariableMetadata = "/xema/v1/variables/mesurades/metadades"
        const val StationDay = "/xema/v1/estacions/mesurades/%s/%04d/%02d/%02d"
    }

    object Fields {
        const val Code = "codi"
        const val Name = "nom"
        const val Coordinates = "coordenades"
        const val Latitude = "latitud"
        const val Longitude = "longitud"
        const val Days = "dies"
        const val Date = "data"
        const val Variables = "variables"
        const val Value = "valor"
        const val Values = "valors"
        const val MaxTemperature = "tmax"
        const val MinTemperature = "tmin"
        const val Rain = "precipitacio"
        const val Sky = "estatCel"
        const val Temperature = "temp"
        const val Humidity = "humitat"
        const val Wind = "velVent"
        const val Stations = "estacions"
        const val Order = "ordre"
        const val Acronym = "acronim"
        const val Readings = "lectures"
    }

    object Acronyms {
        const val Temperature = "T"
        const val Humidity = "HR"
        const val Wind = "VV10"
        const val Rain = "PPT"
    }
}
