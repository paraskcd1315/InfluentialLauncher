// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

sealed interface RunningUpdate {
    data class Started(val entry: TimeEntry) : RunningUpdate
    data class Ended(val id: String) : RunningUpdate
}
