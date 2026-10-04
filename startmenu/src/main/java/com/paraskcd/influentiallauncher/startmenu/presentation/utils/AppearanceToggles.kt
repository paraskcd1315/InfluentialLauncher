// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppearanceToggle

object AppearanceToggles {
    fun of(settings: LauncherSettings): List<AppearanceToggle> = listOf(
        AppearanceToggle("labels", R.string.startmenu_settings_labels, settings.showLabels) { held, on -> held.copy(showLabels = on) },
        AppearanceToggle("clock", R.string.startmenu_settings_clock, settings.showClock) { held, on -> held.copy(showClock = on) },
        AppearanceToggle("glance", R.string.startmenu_settings_glance, settings.showGlance) { held, on -> held.copy(showGlance = on) }
    )
}
