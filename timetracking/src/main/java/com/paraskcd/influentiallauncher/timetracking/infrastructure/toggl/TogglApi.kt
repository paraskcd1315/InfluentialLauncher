// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl

object TogglApi {
    const val BaseUrl = "https://api.track.toggl.com/api/v9"

    object Paths {
        const val Me = "/me"
        const val TimeEntries = "/me/time_entries"
        const val CurrentEntry = "/me/time_entries/current"
        const val Projects = "/me/projects"
        const val WorkspaceEntries = "/workspaces/%s/time_entries"
        const val WorkspaceEntry = "/workspaces/%s/time_entries/%s"
        const val StopEntry = "/workspaces/%s/time_entries/%s/stop"
    }

    object Query {
        const val StartDate = "start_date"
        const val EndDate = "end_date"
    }
}
