// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import androidx.annotation.StringRes
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings

data class AppearanceToggle(
    val key: String,
    @StringRes val labelRes: Int,
    val on: Boolean,
    val apply: (LauncherSettings, Boolean) -> LauncherSettings
)
