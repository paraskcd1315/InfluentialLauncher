// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.settings.domain.model

data class LauncherSettings(
    val showAppsTab: Boolean = true,
    val showCalendarTab: Boolean = true,
    val showContactsTab: Boolean = true,
    val showLabels: Boolean = true,
    val showClock: Boolean = true,
    val showGlance: Boolean = true
)
