// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import android.text.Html
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import java.text.Normalizer

object MeteocatSky {
    private val Diacritics = Regex("\\p{Mn}+")
    private val Keywords = listOf(
        "tempesta" to WeatherCondition.Thunder,
        "aiguaneu" to WeatherCondition.Snow,
        "neu" to WeatherCondition.Snow,
        "nevada" to WeatherCondition.Snow,
        "calamarsa" to WeatherCondition.Snow,
        "plugim" to WeatherCondition.Drizzle,
        "pluja" to WeatherCondition.Rain,
        "xafec" to WeatherCondition.Rain,
        "ruixat" to WeatherCondition.Rain,
        "boira" to WeatherCondition.Fog,
        "boirina" to WeatherCondition.Fog,
        "calitja" to WeatherCondition.Fog,
        "molt ennuvolat" to WeatherCondition.Cloudy,
        "mig ennuvolat" to WeatherCondition.PartlyCloudy,
        "poc ennuvolat" to WeatherCondition.PartlyCloudy,
        "nuvols" to WeatherCondition.PartlyCloudy,
        "ennuvolat" to WeatherCondition.Cloudy,
        "cobert" to WeatherCondition.Cloudy,
        "sere" to WeatherCondition.Clear
    )

    fun conditionOf(name: String?): WeatherCondition {
        val text = fold(name ?: return WeatherCondition.Cloudy)
        return Keywords.firstOrNull { (word, _) -> word in text }?.second ?: WeatherCondition.Cloudy
    }

    private fun fold(text: String): String {
        val decoded = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
        return Normalizer.normalize(decoded, Normalizer.Form.NFD).replace(Diacritics, "").lowercase()
    }
}
