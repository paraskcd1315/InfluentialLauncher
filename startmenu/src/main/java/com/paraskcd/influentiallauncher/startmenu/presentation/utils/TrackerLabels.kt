package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import androidx.annotation.StringRes
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker

object TrackerLabels {
    @StringRes
    fun nameOf(tracker: Tracker): Int = when (tracker) {
        Tracker.Toggl -> R.string.startmenu_tracker_toggl
        Tracker.Kimai -> R.string.startmenu_tracker_kimai
    }
}
