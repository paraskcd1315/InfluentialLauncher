// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo

import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Forecast
import com.paraskcd.influentiallauncher.weather.domain.model.HourForecast
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoApi.Fields
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoApi.Query
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class OpenMeteoProvider @Inject constructor(
    private val http: HttpText
) : WeatherProvider {

    override val name: WeatherSourceName = WeatherSourceName.OpenMeteo

    override fun covers(place: Place): Boolean = true

    override suspend fun forecast(place: Place): Forecast {
        val url = ApiUrl.of(
            OpenMeteoApi.ForecastBaseUrl,
            OpenMeteoApi.Paths.Forecast,
            mapOf(
                Query.Latitude to place.latitude,
                Query.Longitude to place.longitude,
                Query.Current to listOf(Fields.Temperature, Fields.FeelsLike, Fields.Humidity, Fields.Wind, Fields.WeatherCode, Fields.IsDay).joinToString(","),
                Query.Hourly to listOf(Fields.Temperature, Fields.WeatherCode, Fields.IsDay, Fields.RainChance).joinToString(","),
                Query.Daily to listOf(
                    Fields.WeatherCode, Fields.MaxTemperature, Fields.MinTemperature, Fields.MaxRainChance,
                    Fields.RainSum, Fields.UvMax, Fields.Sunrise, Fields.Sunset
                ).joinToString(","),
                Query.ForecastDays to DaysShown,
                Query.Timezone to OpenMeteoApi.AutoTimezone
            )
        )
        val json = JSONObject(http.get(url))
        val current = json.getJSONObject(Query.Current)
        val now = Weather(
            temperatureC = current.getDouble(Fields.Temperature).roundToInt(),
            condition = WeatherCondition.fromWmoCode(current.getInt(Fields.WeatherCode)),
            isDay = current.optInt(Fields.IsDay, 1) == 1,
            place = place.locality
        )
        val hourly = json.getJSONObject(Query.Hourly)
        val times = hourly.getJSONArray(Fields.Time)
        val thisHour = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val hours = (0 until times.length())
            .map { index -> index to LocalDateTime.parse(times.getString(index)) }
            .filter { (_, time) -> !time.isBefore(thisHour) }
            .take(HoursShown)
            .map { (index, time) ->
                HourForecast(
                    time = time,
                    temperatureC = hourly.getJSONArray(Fields.Temperature).getDouble(index).roundToInt(),
                    condition = WeatherCondition.fromWmoCode(hourly.getJSONArray(Fields.WeatherCode).getInt(index)),
                    isDay = hourly.getJSONArray(Fields.IsDay).optInt(index, 1) == 1,
                    rainChancePercent = hourly.getJSONArray(Fields.RainChance).intOrNull(index)
                )
            }
        val daily = json.getJSONObject(Query.Daily)
        val dates = daily.getJSONArray(Fields.Time)
        val days = (0 until dates.length()).map { index ->
            DayForecast(
                date = LocalDate.parse(dates.getString(index)),
                minC = daily.getJSONArray(Fields.MinTemperature).getDouble(index).roundToInt(),
                maxC = daily.getJSONArray(Fields.MaxTemperature).getDouble(index).roundToInt(),
                condition = WeatherCondition.fromWmoCode(daily.getJSONArray(Fields.WeatherCode).getInt(index)),
                rainChancePercent = daily.getJSONArray(Fields.MaxRainChance).intOrNull(index)
            )
        }
        return Forecast(
            now = now,
            feelsLikeC = current.doubleOrNull(Fields.FeelsLike)?.roundToInt(),
            humidityPercent = current.doubleOrNull(Fields.Humidity)?.roundToInt(),
            windKmh = current.doubleOrNull(Fields.Wind)?.roundToInt(),
            rainChancePercent = hours.firstOrNull()?.rainChancePercent,
            rainTodayMm = daily.getJSONArray(Fields.RainSum).doubleOrNull(0),
            uvIndex = daily.getJSONArray(Fields.UvMax).doubleOrNull(0)?.roundToInt(),
            sunrise = daily.getJSONArray(Fields.Sunrise).optString(0).toTimeOrNull(),
            sunset = daily.getJSONArray(Fields.Sunset).optString(0).toTimeOrNull(),
            hours = hours,
            days = days,
            source = name
        )
    }

    private fun JSONObject.doubleOrNull(field: String): Double? = if (isNull(field)) null else optDouble(field).takeUnless { it.isNaN() }

    private fun JSONArray.doubleOrNull(index: Int): Double? = if (isNull(index)) null else optDouble(index).takeUnless { it.isNaN() }

    private fun JSONArray.intOrNull(index: Int): Int? = doubleOrNull(index)?.roundToInt()

    private fun String.toTimeOrNull(): LocalTime? = runCatching { LocalDateTime.parse(this).toLocalTime() }.getOrNull()

    private companion object {
        const val HoursShown = 24
        const val DaysShown = 7
    }
}
