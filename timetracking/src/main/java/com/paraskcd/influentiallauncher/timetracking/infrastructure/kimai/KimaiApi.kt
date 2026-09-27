package com.paraskcd.influentiallauncher.timetracking.infrastructure.kimai

object KimaiApi {
    const val ApiRoot = "/api"

    object Paths {
        const val Timesheets = "/timesheets"
        const val ActiveTimesheets = "/timesheets/active"
        const val Timesheet = "/timesheets/%s"
        const val StopTimesheet = "/timesheets/%s/stop"
        const val Projects = "/projects"
        const val Activities = "/activities"
    }

    object Query {
        const val Begin = "begin"
        const val End = "end"
        const val Full = "full"
        const val Size = "size"
        const val Order = "order"
        const val Visible = "visible"
        const val Project = "project"
    }
}
