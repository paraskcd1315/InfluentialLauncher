// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.domain.ports

import kotlinx.coroutines.flow.StateFlow

interface OpenApps {
    val taskCounts: StateFlow<Map<String, Int>>

    val running: StateFlow<Set<String>>

    fun refresh()

    fun close(packageName: String)
}
