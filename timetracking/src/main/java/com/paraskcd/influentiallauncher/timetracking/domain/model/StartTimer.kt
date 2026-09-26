package com.paraskcd.influentiallauncher.timetracking.domain.model

data class StartTimer(
    val description: String,
    val projectId: String?,
    val activityId: String?
)
