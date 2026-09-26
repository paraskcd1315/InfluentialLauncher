package com.paraskcd.influentiallauncher.startmenu.presentation.utils

import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.model.TabToggle

object TabToggles {
    fun of(settings: LauncherSettings): List<TabToggle> = listOf(
        TabToggle(StartMenuTab.Apps, R.string.startmenu_settings_apps, settings.showAppsTab),
        TabToggle(StartMenuTab.Calendar, R.string.startmenu_settings_calendar, settings.showCalendarTab),
        TabToggle(StartMenuTab.Contacts, R.string.startmenu_settings_contacts, settings.showContactsTab)
    )

    fun labelOf(tab: StartMenuTab): Int = when (tab) {
        StartMenuTab.Apps -> R.string.startmenu_tab_apps
        StartMenuTab.Calendar -> R.string.startmenu_tab_calendar
        StartMenuTab.Contacts -> R.string.startmenu_tab_contacts
        StartMenuTab.Settings -> R.string.startmenu_tab_settings
    }
}
