package com.paraskcd.influentiallauncher.weather.infrastructure.aemet

import android.content.Context
import com.paraskcd.influentiallauncher.weather.BuildConfig
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.Weather
import com.paraskcd.influentiallauncher.weather.domain.ports.WeatherProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos

@Singleton
class AemetProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val http: HttpText
) : WeatherProvider {

    private val key = BuildConfig.AEMET_API_KEY
    private val latin = Charset.forName("ISO-8859-15")
    private val lock = Mutex()
    private var municipalities: List<Municipality>? = null

    override fun covers(place: Place): Boolean = key.isNotBlank() && place.countryCode.equals(SpainCode, ignoreCase = true)

    override suspend fun current(place: Place): Weather? {
        val municipality = nearest(place) ?: return null
        val hours = JSONArray(datos("$BaseUrl/prediccion/especifica/municipio/horaria/${municipality.id}"))
        val days = hours.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia")
        val today = LocalDate.now().toString()
        val hour = "%02d".format(LocalTime.now().hour)
        val day = (0 until days.length()).map { days.getJSONObject(it) }.firstOrNull { it.optString("fecha").startsWith(today) }
            ?: return null
        val sky = day.getJSONArray("estadoCielo").periodValue(hour) ?: return null
        val temperature = day.getJSONArray("temperatura").periodValue(hour)?.toIntOrNull() ?: return null
        return Weather(
            temperatureC = temperature,
            condition = AemetSky.conditionOf(sky),
            isDay = !AemetSky.isNight(sky),
            place = place.locality ?: municipality.name
        )
    }

    private fun JSONArray.periodValue(hour: String): String? {
        val entries = (0 until length()).map { getJSONObject(it) }
        val exact = entries.firstOrNull { it.optString("periodo") == hour }
        val latest = entries.lastOrNull { it.optString("periodo") <= hour }
        return (exact ?: latest ?: entries.firstOrNull())?.optString("value")?.takeIf { it.isNotBlank() }
    }

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
        val cache = File(context.filesDir, CacheFile)
        val text = if (cache.exists()) {
            withContext(Dispatchers.IO) { cache.readText() }
        } else {
            val fetched = datosOrDirect("$BaseUrl/maestro/municipios")
            withContext(Dispatchers.IO) { cache.writeText(fetched) }
            fetched
        }
        val array = JSONArray(text)
        val list = (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val latitude = item.optString("latitud_dec").toDoubleOrNull() ?: return@mapNotNull null
            val longitude = item.optString("longitud_dec").toDoubleOrNull() ?: return@mapNotNull null
            Municipality(
                id = item.optString("id").removePrefix(IdPrefix),
                name = item.optString("nombre"),
                latitude = latitude,
                longitude = longitude
            )
        }
        municipalities = list
        list
    }

    private suspend fun datos(url: String): String {
        val envelope = JSONObject(http.get(url, headers(), latin))
        return http.get(envelope.getString("datos"), fallbackCharset = latin)
    }

    private suspend fun datosOrDirect(url: String): String {
        val body = http.get(url, headers(), latin).trim()
        if (body.startsWith("[")) return body
        return http.get(JSONObject(body).getString("datos"), fallbackCharset = latin)
    }

    private fun headers() = mapOf("api_key" to key)

    private data class Municipality(val id: String, val name: String, val latitude: Double, val longitude: Double)

    private companion object {
        const val BaseUrl = "https://opendata.aemet.es/opendata/api"
        const val SpainCode = "ES"
        const val IdPrefix = "id"
        const val CacheFile = "aemet_municipios.json"
    }
}
