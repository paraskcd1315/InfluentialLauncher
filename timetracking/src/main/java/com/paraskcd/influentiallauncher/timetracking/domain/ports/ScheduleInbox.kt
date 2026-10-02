// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.ports

interface ScheduleInbox {
    suspend fun take(): String?
}
