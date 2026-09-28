// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo

import com.paraskcd.influentiallauncher.weather.domain.model.AirQuality
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.weather.domain.ports.AirQualityProvider
import com.paraskcd.influentiallauncher.weather.infrastructure.http.ApiUrl
import com.paraskcd.influentiallauncher.weather.infrastructure.http.HttpText
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoApi.Fields
import com.paraskcd.influentiallauncher.weather.infrastructure.openmeteo.OpenMeteoApi.Query
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class OpenMeteoAirQuality @Inject constructor(
    private val http: HttpText
) : AirQualityProvider {

    override suspend fun airQuality(place: Place): AirQuality? {
        val url = ApiUrl.of(
            OpenMeteoApi.AirQualityBaseUrl,
            OpenMeteoApi.Paths.AirQuality,
            mapOf(
                Query.Latitude to place.latitude,
                Query.Longitude to place.longitude,
                Query.Current to listOf(Fields.EuropeanAqi, Fields.Pm25, Fields.Pm10).joinToString(","),
                Query.Timezone to OpenMeteoApi.AutoTimezone
            )
        )
        val current = JSONObject(http.get(url)).optJSONObject(Query.Current) ?: return null
        if (current.isNull(Fields.EuropeanAqi)) return null
        return AirQuality(
            europeanAqi = current.getDouble(Fields.EuropeanAqi).roundToInt(),
            pm25 = current.optDouble(Fields.Pm25).takeUnless { it.isNaN() },
            pm10 = current.optDouble(Fields.Pm10).takeUnless { it.isNaN() }
        )
    }
}
