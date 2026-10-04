// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppearanceToggle

object AppearanceToggles {
    fun homeScreen(settings: LauncherSettings): List<AppearanceToggle> = listOf(
        AppearanceToggle("homeLabels", R.string.startmenu_settings_labels, settings.showLabels) { held, on -> held.copy(showLabels = on) },
        AppearanceToggle("clock", R.string.startmenu_settings_clock, settings.showClock) { held, on -> held.copy(showClock = on) },
        AppearanceToggle("glance", R.string.startmenu_settings_glance, settings.showGlance) { held, on -> held.copy(showGlance = on) },
        AppearanceToggle("start", R.string.startmenu_settings_start_button, settings.showStartButton) { held, on -> held.copy(showStartButton = on) }
    )

    fun startMenu(settings: LauncherSettings): List<AppearanceToggle> = listOf(
        AppearanceToggle("startLabels", R.string.startmenu_settings_labels, settings.showStartLabels) { held, on -> held.copy(showStartLabels = on) }
    )

    fun icons(settings: LauncherSettings): List<AppearanceToggle> = listOf(
        AppearanceToggle("dots", R.string.startmenu_settings_running_dots, settings.showRunningDots) { held, on -> held.copy(showRunningDots = on) },
        AppearanceToggle("badges", R.string.startmenu_settings_badges, settings.showBadges) { held, on -> held.copy(showBadges = on) }
    )

    fun colours(settings: LauncherSettings): List<AppearanceToggle> = listOf(
        AppearanceToggle("tint", R.string.startmenu_settings_tint_panels, settings.tintPanels) { held, on -> held.copy(tintPanels = on) }
    )
}
