package com.paraskcd.influentiallauncher.clock.presentation.utils

import android.text.format.DateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ClockFormat {
    private const val TimeSkeleton24 = "Hm"
    private const val TimeSkeleton12 = "hm"
    private const val DateSkeleton = "EEEEdMMMMyyyy"

    fun time(now: ZonedDateTime, locale: Locale, is24Hour: Boolean): String {
        val skeleton = if (is24Hour) TimeSkeleton24 else TimeSkeleton12
        return now.format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, skeleton), locale))
    }

    fun date(now: ZonedDateTime, locale: Locale): String =
        now.format(DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, DateSkeleton), locale))
}
