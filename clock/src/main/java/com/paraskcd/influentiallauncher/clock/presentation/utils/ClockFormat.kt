package com.paraskcd.influentiallauncher.clock.presentation.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ClockFormat {
    private const val TimePattern = "HH:mm"
    private const val DatePattern = "dd MMMM, yyyy"

    fun time(now: ZonedDateTime, locale: Locale): String = now.format(DateTimeFormatter.ofPattern(TimePattern, locale))

    fun date(now: ZonedDateTime, locale: Locale): String = now.format(DateTimeFormatter.ofPattern(DatePattern, locale))
}
