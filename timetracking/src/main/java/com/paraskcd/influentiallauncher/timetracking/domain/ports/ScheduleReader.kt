// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.ports

import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule

interface ScheduleReader {
    fun read(text: String): WorkSchedule?
}
