// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.HourForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.aemet.AemetApi.Fields
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos

@Singleton
class AemetProvider @Inject constructor(
    private val fetcher: AemetFetcher
) : WeatherProvider {

    override val name: WeatherSourceName = WeatherSourceName.Aemet

    private val lock = Mutex()
    private var municipalities: List<Municipality>? = null

    override fun covers(place: Place): Boolean = fetcher.configured && place.countryCode.equals(SpainCode, ignoreCase = true)

    override suspend fun forecast(place: Place): Forecast? {
        val municipality = nearest(place) ?: return null
        val hourlyDays = fetcher.text(HourlyPrefix + municipality.id, AemetApi.Paths.HourlyForecast.format(municipality.id), HourlyMaxAgeMs)
            ?.let(::predictionDays) ?: return null
        val dailyDays = fetcher.text(DailyPrefix + municipality.id, AemetApi.Paths.DailyForecast.format(municipality.id), DailyMaxAgeMs)
            ?.let(::predictionDays).orEmpty()
        val today = LocalDate.now()
        val thisHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val slots = hourlyDays.flatMap { it.hourSlots() }
        val upcoming = slots.filter { !it.time.isBefore(thisHour) }.take(HoursShown)
        val current = upcoming.firstOrNull() ?: slots.lastOrNull() ?: return null
        val todayHourly = hourlyDays.firstOrNull { it.date() == today }
        val days = dailyDays.mapNotNull { it.toDayForecast() }.filter { !it.date.isBefore(today) }.take(DaysShown)
        return Forecast(
            now = Weather(
                temperatureC = current.hour.temperatureC,
                condition = current.hour.condition,
                isDay = current.hour.isDay,
                place = place.locality ?: municipality.name
            ),
            feelsLikeC = current.feelsLikeC,
            humidityPercent = current.humidityPercent,
            windKmh = current.windKmh,
            rainChancePercent = current.hour.rainChancePercent,
            rainTodayMm = todayHourly?.optJSONArray(Fields.Rain)?.let { rain ->
                (0 until rain.length()).sumOf { rain.getJSONObject(it).optString(Fields.Value).toMm() }
            },
            uvIndex = dailyDays.firstOrNull { it.date() == today }?.let { if (it.has(Fields.UvMax)) it.optInt(Fields.UvMax) else null },
            sunrise = todayHourly?.optString(Fields.Sunrise)?.toTimeOrNull(),
            sunset = todayHourly?.optString(Fields.Sunset)?.toTimeOrNull(),
            hours = upcoming.map { it.hour },
            days = days,
            source = name
        )
    }

    private fun JSONObject.hourSlots(): List<Slot> {
        val date = date() ?: return emptyList()
        val sky = optJSONArray(Fields.Sky) ?: return emptyList()
        val temperatures = optJSONArray(Fields.Temperature).byPeriod()
        val feelsLike = optJSONArray(Fields.FeelsLike).byPeriod()
        val humidity = optJSONArray(Fields.Humidity).byPeriod()
        val rainChances = optJSONArray(Fields.RainChance).entries()
        val wind = optJSONArray(Fields.WindAndGust).entries()
            .filter { it.has(Fields.Speed) }
            .associate { it.optString(Fields.Period) to it.optJSONArray(Fields.Speed)?.optString(0) }
        return sky.entries().mapNotNull { entry ->
            val period = entry.optString(Fields.Period)
            val hour = period.toIntOrNull() ?: return@mapNotNull null
            val code = entry.optString(Fields.Value).takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val temperature = temperatures[period]?.toIntOrNull() ?: return@mapNotNull null
            Slot(
                time = date.atTime(hour, 0),
                hour = HourForecast(
                    time = date.atTime(hour, 0),
                    temperatureC = temperature,
                    condition = AemetSky.conditionOf(code),
                    isDay = !AemetSky.isNight(code),
                    rainChancePercent = rainChances.rangeValue(hour)
                ),
                feelsLikeC = feelsLike[period]?.toIntOrNull(),
                humidityPercent = humidity[period]?.toIntOrNull(),
                windKmh = wind[period]?.toIntOrNull()
            )
        }
    }

    private fun JSONObject.toDayForecast(): DayForecast? {
        val date = date() ?: return null
        val temperature = optJSONObject(Fields.Temperature) ?: return null
        val sky = optJSONArray(Fields.Sky).entries().filter { it.optString(Fields.Value).isNotBlank() }
        val code = DayPeriods.firstNotNullOfOrNull { period -> sky.firstOrNull { it.optString(Fields.Period) == period } }
            ?: sky.firstOrNull()
            ?: return null
        val rain = optJSONArray(Fields.RainChance).entries()
        val rainChance = rain.firstOrNull { it.optString(Fields.Period) == WholeDay }
            ?: rain.maxByOrNull { it.optInt(Fields.Value) }
        return DayForecast(
            date = date,
            minC = temperature.optInt(Fields.Minimum),
            maxC = temperature.optInt(Fields.Maximum),
            condition = AemetSky.conditionOf(code.optString(Fields.Value)),
            rainChancePercent = rainChance?.let { if (it.isNull(Fields.Value)) null else it.optInt(Fields.Value) }
        )
    }

    private fun List<JSONObject>.rangeValue(hour: Int): Int? = firstOrNull { entry ->
        val period = entry.optString(Fields.Period)
        val start = period.take(2).toIntOrNull() ?: return@firstOrNull false
        val end = period.drop(2).toIntOrNull() ?: return@firstOrNull false
        if (end > start) hour in start until end else hour >= start || hour < end
    }?.optString(Fields.Value)?.toIntOrNull()

    private fun JSONArray?.entries(): List<JSONObject> =
        if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it) }

    private fun JSONArray?.byPeriod(): Map<String, String> =
        entries().associate { it.optString(Fields.Period) to it.optString(Fields.Value) }

    private fun JSONObject.date(): LocalDate? = runCatching { LocalDate.parse(optString(Fields.Date).take(DateLength)) }.getOrNull()

    private fun String.toMm(): Double = replace(',', '.').toDoubleOrNull() ?: 0.0

    private fun String.toTimeOrNull(): LocalTime? = runCatching { LocalTime.parse(this) }.getOrNull()

    private fun predictionDays(text: String): List<JSONObject>? = runCatching {
        JSONArray(text).getJSONObject(0).getJSONObject(Fields.Prediction).optJSONArray(Fields.Day).entries()
    }.getOrNull()

    private suspend fun nearest(place: Place): Municipality? {
        val all = municipalities() ?: return null
        val scale = cos(Math.toRadians(place.latitude))
        return all.minByOrNull { municipality ->
            val dLat = municipality.latitude - place.latitude
            val dLon = (municipality.longitude - place.longitude) * scale
            dLat * dLat + dLon * dLon
        }
    }

    private suspend fun municipalities(): List<Municipality>? = lock.withLock {
        municipalities?.let { return@withLock it }
        val text = fetcher.text(MunicipalitiesFile, AemetApi.Paths.Municipalities, ReferenceMaxAgeMs) ?: return@withLock null
        val array = runCatching { JSONArray(text) }.getOrNull() ?: return@withLock null
        val list = (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val latitude = item.optString(Fields.Latitude).toDoubleOrNull() ?: return@mapNotNull null
            val longitude = item.optString(Fields.Longitude).toDoubleOrNull() ?: return@mapNotNull null
            Municipality(
                id = item.optString(Fields.Id).removePrefix(IdPrefix),
                name = item.optString(Fields.Name),
                latitude = latitude,
                longitude = longitude
            )
        }
        municipalities = list
        list
    }

    private data class Municipality(val id: String, val name: String, val latitude: Double, val longitude: Double)

    private data class Slot(
        val time: LocalDateTime,
        val hour: HourForecast,
        val feelsLikeC: Int?,
        val humidityPercent: Int?,
        val windKmh: Int?
    )

    private companion object {
        const val SpainCode = "ES"
        const val IdPrefix = "id"
        const val MunicipalitiesFile = "municipios.json"
        const val HourlyPrefix = "hourly_"
        const val DailyPrefix = "daily_"
        const val HourlyMaxAgeMs = 60 * 60 * 1000L
        const val DailyMaxAgeMs = 6 * 60 * 60 * 1000L
        const val ReferenceMaxAgeMs = 90 * 24 * 60 * 60 * 1000L
        const val HoursShown = 24
        const val DaysShown = 7
        const val DateLength = 10
        const val WholeDay = "00-24"
        val DayPeriods = listOf(WholeDay, "12-24", "12-18", "06-12")
    }
}
