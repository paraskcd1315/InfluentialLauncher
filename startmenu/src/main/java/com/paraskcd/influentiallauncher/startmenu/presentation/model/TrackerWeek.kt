// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import java.time.Duration

data class TrackerWeek(
    val tracker: Tracker,
    val entries: List<TimeEntry>,
    val target: Duration?
)
