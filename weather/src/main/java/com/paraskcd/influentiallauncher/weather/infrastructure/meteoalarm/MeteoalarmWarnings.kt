// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteoalarm

import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.model.WarningLevel
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherWarning
import com.paraskcd.influentiallauncher.weather.domain.ports.WarningProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import com.paraskcd.influentiallauncher.weather.infrastructure.meteoalarm.MeteoalarmApi.Fields
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.Normalizer
import java.time.OffsetDateTime
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MeteoalarmWarnings @Inject constructor(
    private val http: HttpText
) : WarningProvider {

    override fun covers(place: Place): Boolean = place.countryCode.equals(SpainCode, ignoreCase = true)

    override suspend fun warnings(place: Place): List<WeatherWarning> {
        val area = (place.province ?: place.locality)?.let(::fold) ?: return emptyList()
        val body = http.get(ApiUrl.of(MeteoalarmApi.BaseUrl, MeteoalarmApi.Paths.SpainWarnings))
        return withContext(Dispatchers.Default) { parse(body, area) }
    }

    private fun parse(body: String, area: String): List<WeatherWarning> {
        val now = OffsetDateTime.now()
        val language = Locale.getDefault().language
        return JSONObject(body).optJSONArray(Fields.Warnings).objects()
            .mapNotNull { it.optJSONObject(Fields.Alert) }
            .filter { it.optString(Fields.MessageType) != Fields.Cancel }
            .flatMap { alert ->
                val infos = alert.optJSONArray(Fields.Info).objects()
                val info = infos.firstOrNull { it.optString(Fields.Language).startsWith(language) } ?: infos.firstOrNull()
                if (info == null) return@flatMap emptyList()
                val expires = info.optString(Fields.Expires).toTimeOrNull()
                if (expires != null && expires.isBefore(now)) return@flatMap emptyList()
                val level = info.optJSONArray(Fields.Parameter).objects()
                    .firstOrNull { it.optString(Fields.ParameterName) == Fields.AwarenessLevel }
                    ?.optString(Fields.ParameterValue)
                    ?.let(WarningLevel::fromAwareness)
                    ?: return@flatMap emptyList()
                info.optJSONArray(Fields.Area).objects()
                    .map { it.optString(Fields.AreaName) }
                    .filter { fold(it).contains(area) }
                    .map { name ->
                        WeatherWarning(
                            headline = info.optString(Fields.Headline).substringBefore(". $name").ifBlank { info.optString(Fields.Headline) },
                            description = info.optString(Fields.Description).takeIf { it.isNotBlank() },
                            areas = listOf(name),
                            level = level,
                            onset = info.optString(Fields.Onset).toTimeOrNull(),
                            expires = expires
                        )
                    }
            }
            .groupBy { listOf(it.headline, it.level, it.onset, it.expires) }
            .values
            .map { same ->
                same.first().copy(
                    description = same.mapNotNull { it.description }.distinct().joinToString("\n").ifBlank { null },
                    areas = same.flatMap { it.areas }.distinct()
                )
            }
            .sortedWith(compareByDescending<WeatherWarning> { it.level }.thenBy { it.onset })
    }

    private fun JSONArray?.objects(): List<JSONObject> =
        if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it) }

    private fun String.toTimeOrNull(): OffsetDateTime? = runCatching { OffsetDateTime.parse(this) }.getOrNull()

    private fun fold(value: String): String =
        Normalizer.normalize(value, Normalizer.Form.NFD).replace(Accents, "").lowercase(Locale.ROOT)

    private companion object {
        const val SpainCode = "ES"
        val Accents = Regex("\\p{Mn}+")
    }
}
