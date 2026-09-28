// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

data class StartTimer(
    val description: String,
    val projectId: String?,
    val activityId: String?
)
