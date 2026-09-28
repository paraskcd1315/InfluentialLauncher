// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

import java.time.Instant

data class TimeEntry(
    val id: String,
    val tracker: Tracker,
    val description: String,
    val projectId: String?,
    val projectName: String?,
    val colourArgb: Int?,
    val start: Instant,
    val end: Instant?,
    val activityName: String? = null,
    val clientName: String? = null,
    val tags: List<String> = emptyList()
) {
    val running: Boolean get() = end == null
}
