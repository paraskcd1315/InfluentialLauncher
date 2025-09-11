package com.paraskcd.influentiallauncher.services

import com.google.gson.Gson
import com.paraskcd.influentiallauncher.data.types.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class WeatherService @Inject constructor(
    private val httpClient: OkHttpClient
) {
    private val gson = Gson()

    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast" +
                        "?latitude=$latitude" +
                        "&longitude=$longitude" +
                        "&current=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m" +
                        "&daily=temperature_2m_max,temperature_2m_min,weather_code" +
                        "&timezone=auto" +
                        "&forecast_days=7"

                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val weatherResponse = gson.fromJson(responseBody, WeatherResponse::class.java)
                    Result.success(weatherResponse)
                } else {
                    Result.failure(Exception("Failed to fetch weather data: ${response.code}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}