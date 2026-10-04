// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.weather.domain.usecase

import com.paraskcd.influentiallauncher.weather.domain.model.DayForecast
import com.paraskcd.influentiallauncher.weather.domain.model.DayPart
import com.paraskcd.influentiallauncher.weather.domain.model.HourForecast
import kotlin.math.abs

object HourlyDays {
    private const val PartHours = 6
    private const val HoursInDay = 24

    fun enrich(day: DayForecast, hours: List<HourForecast>): DayForecast {
        val ofDay = hours.filter { it.time.toLocalDate() == day.date }
        if (ofDay.isEmpty()) return day
        val feelsLike = ofDay.mapNotNull { it.feelsLikeC }
        val humidity = ofDay.mapNotNull { it.humidityPercent }
        val windiest = windiestOf(ofDay)
        return day.copy(
            feelsLikeMinC = day.feelsLikeMinC ?: feelsLike.minOrNull(),
            feelsLikeMaxC = day.feelsLikeMaxC ?: feelsLike.maxOrNull(),
            humidityMinPercent = day.humidityMinPercent ?: humidity.minOrNull(),
            humidityMaxPercent = day.humidityMaxPercent ?: humidity.maxOrNull(),
            windKmh = day.windKmh ?: windiest?.windKmh,
            windFrom = day.windFrom ?: windiest?.windFrom,
            parts = day.parts.ifEmpty { parts(ofDay) }
        )
    }

    private fun parts(ofDay: List<HourForecast>): List<DayPart> =
        (0 until HoursInDay step PartHours).mapNotNull { start ->
            val end = start + PartHours
            val inPart = ofDay.filter { it.time.hour in start until end }
            val middle = inPart.minByOrNull { abs(it.time.hour - (start + PartHours / 2)) } ?: return@mapNotNull null
            val windiest = windiestOf(inPart)
            DayPart(
                startHour = start,
                endHour = end,
                condition = middle.condition,
                isDay = middle.isDay,
                rainChancePercent = inPart.mapNotNull { it.rainChancePercent }.maxOrNull(),
                windKmh = windiest?.windKmh,
                windFrom = windiest?.windFrom
            )
        }

    private fun windiestOf(hours: List<HourForecast>): HourForecast? =
        hours.filter { it.windKmh != null }.maxByOrNull { it.windKmh ?: 0 }
}
