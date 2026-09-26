package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import android.text.format.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object EventTimes {
    private const val Time24 = "Hm"
    private const val Time12 = "hm"
    private const val DaySkeleton = "EEEEdMMMM"

    fun time(instant: Instant, locale: Locale, is24Hour: Boolean): String {
        val pattern = DateFormat.getBestDateTimePattern(locale, if (is24Hour) Time24 else Time12)
        return instant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(pattern, locale))
    }

    fun day(day: LocalDate, locale: Locale): String =
        day.format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, DaySkeleton), locale))
}
