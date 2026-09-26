package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

data class StartMenuApp(
    val app: LauncherApp,
    val pinned: Boolean
)
