// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

data class StartMenuApp(
    val app: LauncherApp,
    val onTaskbar: Boolean,
    val onStart: Boolean
)
