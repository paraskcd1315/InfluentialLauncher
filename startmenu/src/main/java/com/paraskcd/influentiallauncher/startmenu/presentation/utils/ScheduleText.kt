// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object ScheduleText {
    const val MaxWeeklyHours = 168.0

    private const val HoursPattern = "0.##"
    private val DayFormat = DateTimeFormatter.ofPattern("EEE d MMM yyyy")

    fun hours(value: Double): String = DecimalFormat(HoursPattern).format(value)

    fun hoursOf(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it in 0.0..MaxWeeklyHours }

    fun weekday(day: DayOfWeek): String =
        day.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }

    fun day(date: LocalDate): String = date.format(DayFormat)

    fun month(number: Int, style: TextStyle = TextStyle.FULL_STANDALONE): String =
        Month.of(number).getDisplayName(style, Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) }
}
