// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry

sealed interface TimelineDetail {
    data class Entry(val entry: TimeEntry) : TimelineDetail
    data class Event(val event: CalendarEvent) : TimelineDetail
}
