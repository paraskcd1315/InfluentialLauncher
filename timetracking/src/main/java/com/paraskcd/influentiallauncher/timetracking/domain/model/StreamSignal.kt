// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

sealed interface StreamSignal {
    data object Open : StreamSignal
    data object Closed : StreamSignal
    data class Changed(val update: RunningUpdate?) : StreamSignal
}
