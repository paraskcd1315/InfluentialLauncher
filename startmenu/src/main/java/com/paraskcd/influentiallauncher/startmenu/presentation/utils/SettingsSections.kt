// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.startmenu.presentation.model.SettingsSection

object SettingsSections {
    fun of(trackersAvailable: Boolean): List<SettingsSection> =
        SettingsSection.entries.filter { it != SettingsSection.Schedule || trackersAvailable }
}
