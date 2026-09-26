package com.paraskcd.influentiallauncher.taskbar.presentation.model

import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

data class PickerRow(
    val app: LauncherApp,
    val pinned: Boolean
)
