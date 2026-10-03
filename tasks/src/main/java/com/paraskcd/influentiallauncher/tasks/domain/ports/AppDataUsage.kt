// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.domain.ports

import com.paraskcd.influentiallauncher.tasks.domain.model.DayData

interface AppDataUsage {
    suspend fun weekly(packageName: String): List<DayData>
}
