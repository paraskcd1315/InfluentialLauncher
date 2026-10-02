// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import java.time.LocalDate

data class DayOffRun(val days: List<LocalDate>) {
    val from: LocalDate get() = days.first()
    val to: LocalDate get() = days.last()
}
