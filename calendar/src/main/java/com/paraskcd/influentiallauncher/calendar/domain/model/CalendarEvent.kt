package com.paraskcd.influentiallauncher.calendar.domain.model

import java.time.Instant

data class CalendarEvent(
    val id: Long,
    val title: String,
    val begin: Instant,
    val end: Instant,
    val allDay: Boolean,
    val colorArgb: Int?,
    val location: String?
)
