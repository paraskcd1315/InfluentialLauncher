package com.paraskcd.influentiallauncher.weather.infrastructure.meteocat

import com.paraskcd.influentiallauncher.weather.domain.model.WeatherCondition
import java.text.Normalizer

object MeteocatSky {
    private val Diacritics = Regex("\\p{Mn}+")
    private val Keywords = listOf(
        "tempesta" to WeatherCondition.Thunder,
        "neu" to WeatherCondition.Snow,
        "aiguaneu" to WeatherCondition.Snow,
        "calamarsa" to WeatherCondition.Snow,
        "plugim" to WeatherCondition.Drizzle,
        "pluja" to WeatherCondition.Rain,
        "xafec" to WeatherCondition.Rain,
        "ruixat" to WeatherCondition.Rain,
        "boira" to WeatherCondition.Fog,
        "broma" to WeatherCondition.Fog,
        "cobert" to WeatherCondition.Cloudy,
        "molt ennuvolat" to WeatherCondition.Cloudy,
        "ennuvolat" to WeatherCondition.PartlyCloudy,
        "nuvol" to WeatherCondition.PartlyCloudy,
        "sere" to WeatherCondition.Clear
    )

    fun conditionOf(name: String?): WeatherCondition {
        val text = fold(name ?: return WeatherCondition.Cloudy)
        return Keywords.firstOrNull { (word, _) -> word in text }?.second ?: WeatherCondition.Cloudy
    }

    fun isNight(name: String?): Boolean = name != null && "nit" in fold(name)

    private fun fold(text: String): String =
        Normalizer.normalize(text, Normalizer.Form.NFD).replace(Diacritics, "").lowercase()
}
