package com.paraskcd.influentiallauncher.timetracking.domain.model

data class TrackerCredentials(
    val togglToken: String = "",
    val kimaiUrl: String = "",
    val kimaiToken: String = ""
) {
    fun configured(tracker: Tracker): Boolean = when (tracker) {
        Tracker.Toggl -> togglToken.isNotBlank()
        Tracker.Kimai -> kimaiUrl.isNotBlank() && kimaiToken.isNotBlank()
    }
}
