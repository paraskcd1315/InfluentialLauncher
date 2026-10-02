// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.domain.model

data class TrackerCredentials(
    val togglToken: String = "",
    val kimaiUrl: String = "",
    val kimaiToken: String = "",
    val workSchedule: String = ""
) {
    val access: TrackerCredentials get() = copy(workSchedule = "")

    fun configured(tracker: Tracker): Boolean = when (tracker) {
        Tracker.Toggl -> togglToken.isNotBlank()
        Tracker.Kimai -> kimaiUrl.isNotBlank() && kimaiToken.isNotBlank()
    }
}
