package com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class OpenMeteoProvider @Inject constructor(
    private val http: HttpText
) : WeatherProvider {

    override fun covers(place: Place): Boolean = true

    override suspend fun current(place: Place): Weather {
        val body = http.get(String.format(Locale.US, ForecastUrl, place.latitude, place.longitude))
        val current = JSONObject(body).getJSONObject("current")
        return Weather(
            temperatureC = current.getDouble("temperature_2m").roundToInt(),
            condition = WeatherCondition.fromWmoCode(current.getInt("weather_code")),
            isDay = current.optInt("is_day", 1) == 1,
            place = place.locality
        )
    }

    private companion object {
        const val ForecastUrl = "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,weather_code,is_day&timezone=auto"
    }
}
