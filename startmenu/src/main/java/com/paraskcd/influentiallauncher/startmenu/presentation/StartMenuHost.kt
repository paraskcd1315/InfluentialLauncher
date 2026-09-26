package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.AppsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.CalendarTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.ContactsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.SettingsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.StartMenuViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.LetterBubbleWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartMenuWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartTabsWindow
import com.paraskcd.influentiallauncher.windowing.presentation.WindowMetrics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartMenuHost(
    open: Boolean,
    tabsOffsetX: Dp,
    tabsOffsetY: Dp,
    bottomOffset: Dp,
    onClose: () -> Unit,
    viewModel: StartMenuViewModel = hiltViewModel()
) {
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var selectedName by rememberSaveable { mutableStateOf(StartMenuTab.Apps.name) }
    val selected = tabs.firstOrNull { it.name == selectedName } ?: tabs.first()
    var scrubLetter by remember { mutableStateOf<Char?>(null) }
    LaunchedEffect(open, selected) { scrubLetter = null }

    val density = LocalDensity.current
    val screenHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val statusTop = with(density) { WindowInsets.statusBarsIgnoringVisibility.getTop(density).toDp() }
    val ime = with(density) { WindowInsets.ime.getBottom(density).toDp() }
    val effectiveBottom = if (ime > bottomOffset) maxOf(bottomOffset, ime + WindowMetrics.ImeGap) else bottomOffset
    val menuTop = statusTop + StartMenuMetrics.windowGap
    val menuHeight = screenHeight - menuTop - effectiveBottom
    val bubbleOffset = effectiveBottom + (menuHeight - DsMetrics.bubbleSize) / 2f

    StartTabsWindow(
        open = open,
        offsetX = tabsOffsetX,
        offsetY = tabsOffsetY,
        tabs = tabs,
        selected = selected,
        onSelect = { selectedName = it.name },
        onClose = onClose
    )
    StartMenuWindow(open = open, offsetY = bottomOffset, height = menuHeight, onClose = onClose) {
        when (selected) {
            StartMenuTab.Apps -> AppsTab(open = open, onClose = onClose, onScrub = { scrubLetter = it })
            StartMenuTab.Calendar -> CalendarTab(open = open, onClose = onClose)
            StartMenuTab.Contacts -> ContactsTab(open = open, onClose = onClose, onScrub = { scrubLetter = it })
            StartMenuTab.Settings -> SettingsTab(settings = settings, onTabShown = viewModel::setTabShown)
        }
    }
    LetterBubbleWindow(letter = scrubLetter.takeIf { open }, offsetY = bubbleOffset)
}
