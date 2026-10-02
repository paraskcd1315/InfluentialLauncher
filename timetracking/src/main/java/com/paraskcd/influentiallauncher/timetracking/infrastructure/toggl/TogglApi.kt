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

    object Stream {
        const val Url = "wss://track.toggl.com/stream"
        const val Origin = "https://api.track.toggl.com"
        const val OriginHeader = "Origin"
        const val TypeField = "type"
        const val TokenField = "api_token"
        const val ModelField = "model"
        const val Authenticate = "authenticate"
        const val Ping = "ping"
        const val Pong = "pong"
        const val TimeEntryModel = "time_entry"
        const val ActionField = "action"
        const val DataField = "data"
        const val Delete = "DELETE"

        object Fields {
            const val Id = "id"
            const val Description = "description"
            const val Start = "start"
            const val Stop = "stop"
            const val Duration = "duration"
            const val DeletedAt = "server_deleted_at"
            const val Tags = "tags"
            val Project = listOf("project_id", "pid")
        }
    }
}
