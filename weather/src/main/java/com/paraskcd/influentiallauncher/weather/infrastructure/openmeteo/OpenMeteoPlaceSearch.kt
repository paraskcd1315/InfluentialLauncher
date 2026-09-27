package com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.ports.PlaceSearch
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoApi.Geocoding
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenMeteoPlaceSearch @Inject constructor(
    private val http: HttpText
) : PlaceSearch {

    override suspend fun search(query: String): List<Place> {
        val text = query.trim()
        if (text.length < MinQuery) return emptyList()
        val url = ApiUrl.of(
            OpenMeteoApi.GeocodingBaseUrl,
            OpenMeteoApi.Paths.Search,
            mapOf(
                Geocoding.Name to text,
                Geocoding.Count to MaxResults,
                Geocoding.Language to Locale.getDefault().language,
                Geocoding.Format to OpenMeteoApi.JsonFormat
            )
        )
        val results = JSONObject(http.get(url)).optJSONArray(Geocoding.Results) ?: return emptyList()
        return (0 until results.length()).mapNotNull { index ->
            val item = results.optJSONObject(index) ?: return@mapNotNull null
            Place(
                latitude = item.getDouble(Geocoding.Latitude),
                longitude = item.getDouble(Geocoding.Longitude),
                locality = item.optString(Geocoding.Name).takeIf { it.isNotBlank() },
                province = item.optString(Geocoding.Province).takeIf { it.isNotBlank() }?.let(::withoutPrefix),
                region = item.optString(Geocoding.Region).takeIf { it.isNotBlank() }?.let(::withoutPrefix),
                countryCode = item.optString(Geocoding.CountryCode).takeIf { it.isNotBlank() }
            )
        }.distinctBy { it.key() }
    }

    private fun withoutPrefix(name: String): String =
        AdminPrefixes.firstOrNull { name.startsWith(it, ignoreCase = true) }?.let { name.substring(it.length) } ?: name

    private companion object {
        const val MinQuery = 2
        const val MaxResults = 8
        val AdminPrefixes = listOf("Provincia de ", "Província de ", "Comunidad Autónoma de ", "Comunitat Autònoma de ")
    }
}
