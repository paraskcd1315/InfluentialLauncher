// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.CalendarClock
import com.composables.icons.lucide.LayoutGrid
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Paintbrush
import com.composables.icons.lucide.Palette
import com.composables.icons.lucide.SquareTerminal
import com.paraskcd.influentiallauncher.startmenu.R

enum class SettingsSection(@StringRes val titleRes: Int, @StringRes val captionRes: Int, val icon: ImageVector) {
    StartMenu(R.string.startmenu_settings_section, R.string.startmenu_settings_section_caption, Lucide.LayoutGrid),
    Appearance(R.string.startmenu_settings_appearance, R.string.startmenu_settings_appearance_caption, Lucide.Palette),
    Accent(R.string.startmenu_accent_section, R.string.startmenu_settings_accent_caption, Lucide.Paintbrush),
    Shell(R.string.startmenu_shell_section, R.string.startmenu_settings_shell_caption, Lucide.SquareTerminal),
    Schedule(R.string.startmenu_settings_schedule, R.string.startmenu_settings_schedule_caption, Lucide.CalendarClock)
}
