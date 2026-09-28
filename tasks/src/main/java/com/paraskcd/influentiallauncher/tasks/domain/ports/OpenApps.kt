package com.paraskcd.influentiallauncher.tasks.domain.ports

import kotlinx.coroutines.flow.StateFlow

interface OpenApps {
    val taskCounts: StateFlow<Map<String, Int>>

    fun refresh()
}
