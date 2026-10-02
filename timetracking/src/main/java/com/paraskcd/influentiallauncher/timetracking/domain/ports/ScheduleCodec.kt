// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.ports

import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule

interface ScheduleCodec {
    fun read(text: String): WorkSchedule?

    fun write(schedule: WorkSchedule): String
}
