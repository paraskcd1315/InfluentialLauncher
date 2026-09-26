package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import java.time.LocalDate

data class TrackerDay(
    val tracker: Tracker,
    val date: LocalDate,
    val entries: List<TimeEntry>,
    val loading: Boolean,
    val failed: Boolean
)
