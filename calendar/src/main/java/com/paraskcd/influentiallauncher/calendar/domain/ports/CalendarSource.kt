package com.paraskcd.influentiallauncher.calendar.domain.ports

import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CalendarSource {
    val permission: String

    fun hasPermission(): Boolean

    fun events(day: LocalDate): Flow<List<CalendarEvent>>

    fun open(event: CalendarEvent): Boolean
}
