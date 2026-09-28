// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.HourForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.meteocat.MeteocatApi.Fields
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Singleton
class MeteocatProvider @Inject constructor(
    private val fetcher: MeteocatFetcher
) : WeatherProvider {

    override val name: WeatherSourceName = WeatherSourceName.Meteocat

    override fun covers(place: Place): Boolean = fetcher.configured && inCatalonia(place)

    override suspend fun forecast(place: Place): Forecast? {
        val municipality = nearest(place) ?: return null
        val symbols = symbols()
        val daily = fetcher.text(DailyPrefix + municipality.code, MeteocatApi.Paths.DailyForecast.format(municipality.code), MeteocatPlan.Forecast, ForecastMaxAgeMs)
            ?.let { days(JSONObject(it), symbols) }.orEmpty()
        val hourly = fetcher.text(HourlyPrefix + municipality.code, MeteocatApi.Paths.HourlyForecast.format(municipality.code), MeteocatPlan.Forecast, ForecastMaxAgeMs)
            ?.let { hours(JSONObject(it), symbols) }.orEmpty()
        if (daily.isEmpty() && hourly.isEmpty()) return null
        val readings = readings(municipality)
        val today = LocalDate.now()
        val thisHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val upcoming = hourly.filter { !it.hour.time.isBefore(thisHour) }.take(HoursShown)
        val current = upcoming.firstOrNull() ?: hourly.lastOrNull()
        val todayDaily = daily.firstOrNull { it.date == today }
        val temperature = readings[MeteocatApi.Acronyms.Temperature]?.roundToInt() ?: current?.hour?.temperatureC ?: return null
        return Forecast(
            now = Weather(
                temperatureC = temperature,
                condition = current?.hour?.condition ?: todayDaily?.condition ?: return null,
                isDay = current?.hour?.isDay ?: true,
                place = place.locality ?: municipality.name
            ),
            feelsLikeC = current?.feelsLikeC,
            humidityPercent = readings[MeteocatApi.Acronyms.Humidity]?.roundToInt() ?: current?.humidityPercent,
            windKmh = readings[MeteocatApi.Acronyms.Wind]?.let { (it * MetresPerSecondToKmh).roundToInt() } ?: current?.windKmh,
            rainChancePercent = todayDaily?.rainChancePercent,
            rainTodayMm = readings[RainTotal],
            uvIndex = null,
            sunrise = null,
            sunset = null,
            hours = upcoming.map { it.hour },
            days = daily.filter { !it.date.isBefore(today) }.take(DaysShown),
            source = name
        )
    }

    private fun days(root: JSONObject, symbols: Map<String, String>): List<DayForecast> =
        root.optJSONArray(Fields.Days).objects().mapNotNull { day ->
            val date = runCatching { LocalDate.parse(day.optString(Fields.Date).take(DateLength)) }.getOrNull() ?: return@mapNotNull null
            val variables = day.optJSONObject(Fields.Variables) ?: return@mapNotNull null
            val max = variables.value(Fields.MaxTemperature) ?: return@mapNotNull null
            val min = variables.value(Fields.MinTemperature) ?: return@mapNotNull null
            DayForecast(
                date = date,
                minC = min.roundToInt(),
                maxC = max.roundToInt(),
                condition = MeteocatSky.conditionOf(symbols[variables.optJSONObject(Fields.Sky)?.optString(Fields.Value)]),
                rainChancePercent = variables.value(Fields.Rain)?.roundToInt()
            )
        }

    private fun hours(root: JSONObject, symbols: Map<String, String>): List<Slot> =
        root.optJSONArray(Fields.Days).objects().flatMap { day ->
            val variables = day.optJSONObject(Fields.Variables) ?: return@flatMap emptyList()
            val sky = variables.series(Fields.Sky)
            val feelsLike = variables.series(Fields.FeelsLike)
            val humidity = variables.series(Fields.Humidity)
            val wind = variables.series(Fields.Wind)
            variables.series(Fields.Temperature).mapNotNull { (time, value) ->
                val temperature = value.toDoubleOrNull() ?: return@mapNotNull null
                Slot(
                    hour = HourForecast(
                        time = time,
                        temperatureC = temperature.roundToInt(),
                        condition = MeteocatSky.conditionOf(symbols[sky[time]]),
                        isDay = time.hour in DayHours,
                        rainChancePercent = null
                    ),
                    feelsLikeC = feelsLike[time]?.toDoubleOrNull()?.roundToInt(),
                    humidityPercent = humidity[time]?.toDoubleOrNull()?.roundToInt(),
                    windKmh = wind[time]?.toDoubleOrNull()?.roundToInt()
                )
            }
        }.sortedBy { it.hour.time }

    private suspend fun readings(municipality: Municipality): Map<String, Double> {
        val station = station(municipality) ?: return emptyMap()
        val codes = variableCodes()
        val today = LocalDate.now(ZoneOffset.UTC)
        val path = MeteocatApi.Paths.StationDay.format(station, today.year, today.monthValue, today.dayOfMonth)
        val text = fetcher.text(ReadingsPrefix + station, path, MeteocatPlan.Stations, ReadingsMaxAgeMs) ?: return emptyMap()
        val variables = runCatching { JSONArray(text) }.getOrNull().objects()
            .firstOrNull()?.optJSONArray(Fields.Variables).objects()
            .associateBy { it.optInt(Fields.Code) }
        val result = mutableMapOf<String, Double>()
        listOf(MeteocatApi.Acronyms.Temperature, MeteocatApi.Acronyms.Humidity, MeteocatApi.Acronyms.Wind).forEach { acronym ->
            val lectures = variables[codes[acronym]]?.optJSONArray(Fields.Readings).objects()
            lectures.lastOrNull()?.optDouble(Fields.Value)?.takeUnless { it.isNaN() }?.let { result[acronym] = it }
        }
        variables[codes[MeteocatApi.Acronyms.Rain]]?.optJSONArray(Fields.Readings).objects()
            .map { it.optDouble(Fields.Value) }.filterNot { it.isNaN() }
            .takeIf { it.isNotEmpty() }?.let { result[RainTotal] = it.sum() }
        return result
    }

    private suspend fun station(municipality: Municipality): String? {
        val path = MeteocatApi.Paths.RepresentativeStations.format(municipality.code, MeteocatApi.TemperatureVariable)
        val text = fetcher.text(StationPrefix + municipality.code, path, MeteocatPlan.Stations, StationMaxAgeMs) ?: return null
        return runCatching { JSONArray(text) }.getOrNull().objects()
            .flatMap { it.optJSONArray(Fields.Variables).objects() }
            .flatMap { it.optJSONArray(Fields.Stations).objects() }
            .minByOrNull { it.optInt(Fields.Order, Int.MAX_VALUE) }
            ?.optString(Fields.Code)?.takeIf { it.isNotBlank() }
    }

    private suspend fun variableCodes(): Map<String, Int> {
        val text = fetcher.text(VariablesFile, MeteocatApi.Paths.VariableMetadata, MeteocatPlan.Stations, ReferenceMaxAgeMs) ?: return emptyMap()
        return runCatching { JSONArray(text) }.getOrNull().objects()
            .associate { it.optString(Fields.Acronym) to it.optInt(Fields.Code) }
    }

    private suspend fun symbols(): Map<String, String> {
        val text = fetcher.text(SymbolsFile, MeteocatApi.Paths.Symbols, null, ReferenceMaxAgeMs) ?: return emptyMap()
        return runCatching { JSONArray(text) }.getOrNull().objects()
            .firstOrNull { it.optString(Fields.Name) == SkyGroup }
            ?.optJSONArray(Fields.Values).objects()
            ?.associate { it.optString(Fields.Code) to it.optString(Fields.Name) }
            .orEmpty()
    }

    private suspend fun nearest(place: Place): Municipality? {
        val text = fetcher.text(MunicipalitiesFile, MeteocatApi.Paths.Municipalities, null, ReferenceMaxAgeMs) ?: return null
        val scale = cos(Math.toRadians(place.latitude))
        return runCatching { JSONArray(text) }.getOrNull().objects().mapNotNull { item ->
            val coordinates = item.optJSONObject(Fields.Coordinates) ?: return@mapNotNull null
            val dLat = coordinates.optDouble(Fields.Latitude) - place.latitude
            val dLon = (coordinates.optDouble(Fields.Longitude) - place.longitude) * scale
            val km = sqrt(dLat * dLat + dLon * dLon) * KmPerDegree
            if (km.isNaN()) null else Municipality(item.optString(Fields.Code), item.optString(Fields.Name), km)
        }.minByOrNull { it.km }?.takeIf { it.km <= MaxMunicipalityKm }
    }

    private fun inCatalonia(place: Place): Boolean {
        if (!place.countryCode.equals(SpainCode, ignoreCase = true)) return false
        val region = place.region.orEmpty().lowercase()
        val province = place.province.orEmpty().lowercase()
        return CataloniaNames.any { it in region } || province in CatalanProvinces
    }

    private fun JSONObject.value(name: String): Double? =
        optJSONObject(name)?.optDouble(Fields.Value)?.takeUnless { it.isNaN() }

    private fun JSONObject.series(name: String): Map<LocalDateTime, String> {
        val variable = optJSONObject(name) ?: return emptyMap()
        val entries = variable.optJSONArray(Fields.Values) ?: variable.optJSONArray(Fields.Value) ?: return emptyMap()
        return entries.objects().mapNotNull { entry ->
            val time = runCatching {
                OffsetDateTime.parse(entry.optString(Fields.Date)).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            }.getOrNull() ?: return@mapNotNull null
            time to entry.optString(Fields.Value)
        }.toMap()
    }

    private fun JSONArray?.objects(): List<JSONObject> =
        if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it) }

    private data class Municipality(val code: String, val name: String, val km: Double)

    private data class Slot(val hour: HourForecast, val feelsLikeC: Int?, val humidityPercent: Int?, val windKmh: Int?)

    private companion object {
        const val SpainCode = "ES"
        const val SkyGroup = "cel"
        const val RainTotal = "PPT_TOTAL"
        const val MunicipalitiesFile = "municipis.json"
        const val SymbolsFile = "simbols.json"
        const val VariablesFile = "variables.json"
        const val DailyPrefix = "daily_"
        const val HourlyPrefix = "hourly_"
        const val StationPrefix = "station_"
        const val ReadingsPrefix = "readings_"
        const val ForecastMaxAgeMs = 20 * 60 * 60 * 1000L
        const val ReadingsMaxAgeMs = 75 * 60 * 1000L
        const val StationMaxAgeMs = 30 * 24 * 60 * 60 * 1000L
        const val ReferenceMaxAgeMs = 90 * 24 * 60 * 60 * 1000L
        const val MetresPerSecondToKmh = 3.6
        const val KmPerDegree = 111.2
        const val MaxMunicipalityKm = 15.0
        const val HoursShown = 24
        const val DaysShown = 7
        const val DateLength = 10
        val DayHours = 7..20
        val CataloniaNames = listOf("catalonia", "catalunya", "cataluña")
        val CatalanProvinces = setOf("barcelona", "tarragona", "lleida", "lérida", "girona", "gerona")
    }
}
