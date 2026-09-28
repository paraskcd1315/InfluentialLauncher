// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.windowing.presentation.isLandscape
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.HeaderPlacement
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.apps.AppsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.CalendarTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.contacts.ContactsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.SettingsTab
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.AppsViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.ContactsViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.StartMenuViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.LetterBubbleWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartMenuWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartSearchWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.StartTabsWindow
import com.paraskcd.influentiallauncher.startmenu.presentation.shared.sheets.StartTimerSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.TimeTrackingViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.TimerButtonWindow
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.windowing.presentation.WindowMetrics
import kotlinx.coroutines.delay

private const val TimerPollMs = 30_000L

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartMenuHost(
    open: Boolean,
    tabsOffsetY: Dp,
    bottomOffset: Dp,
    endOffset: Dp,
    onClose: () -> Unit,
    onAppLaunched: () -> Unit,
    swipe: StartSwipe,
    viewModel: StartMenuViewModel = hiltViewModel(),
    appsViewModel: AppsViewModel = hiltViewModel(),
    contactsViewModel: ContactsViewModel = hiltViewModel(),
    timeTracking: TimeTrackingViewModel = hiltViewModel()
) {
    val credentials by timeTracking.credentials.collectAsStateWithLifecycle()
    val running by timeTracking.running.collectAsStateWithLifecycle()
    val calendarTracker by timeTracking.tracker.collectAsStateWithLifecycle()
    var startFor by remember { mutableStateOf<Tracker?>(null) }
    LaunchedEffect(open, credentials) {
        if (!open) {
            startFor = null
            return@LaunchedEffect
        }
        while (true) {
            timeTracking.refreshRunning()
            delay(TimerPollMs)
        }
    }
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val appsQuery by appsViewModel.query.collectAsStateWithLifecycle()
    val contactsQuery by contactsViewModel.query.collectAsStateWithLifecycle()
    val contactsPermission by contactsViewModel.permissionState.collectAsStateWithLifecycle()
    var selectedName by rememberSaveable { mutableStateOf(StartMenuTab.Apps.name) }
    LaunchedEffect(open) { if (open) selectedName = StartMenuTab.Apps.name }
    val selected = tabs.firstOrNull { it.name == selectedName } ?: tabs.first()
    var scrubLetter by remember { mutableStateOf<Char?>(null) }
    LaunchedEffect(open, selected) { scrubLetter = null }

    val density = LocalDensity.current
    val container = LocalWindowInfo.current.containerSize
    val screenHeight = with(density) { container.height.toDp() }
    val screenWidth = with(density) { container.width.toDp() }
    val statusTop = with(density) { WindowInsets.statusBarsIgnoringVisibility.getTop(density).toDp() }
    val ime = with(density) { WindowInsets.ime.getBottom(density).toDp() }
    val landscape = isLandscape()
    val layoutDirection = LocalLayoutDirection.current
    val navigationBottom = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
    val cutoutStart = with(density) { WindowInsets.displayCutout.getLeft(density, layoutDirection).toDp() }
    val tabsBottom = navigationBottom + StartMenuMetrics.windowGap
    val baseBottom = if (landscape) tabsBottom + StartMenuMetrics.tabsStrip + StartMenuMetrics.windowGap else bottomOffset
    val effectiveBottom = if (ime > baseBottom) maxOf(baseBottom, ime + WindowMetrics.ImeGap) else baseBottom
    val menuTop = statusTop + StartMenuMetrics.windowGap
    val menuHeight = screenHeight - menuTop - effectiveBottom
    val menuEnd = endOffset
    val menuStart = cutoutStart + StartMenuMetrics.windowGap
    val menuWidth = if (landscape) screenWidth - menuStart - menuEnd else screenWidth * StartMenuMetrics.widthFraction
    val menuFraction = menuWidth / screenWidth
    val bubbleOffset = effectiveBottom + (menuHeight - DsMetrics.bubbleSize) / 2f
    val searchOffset = screenHeight - menuTop - StartMenuMetrics.searchTop - DsMetrics.searchHeight
    val searchFraction = (menuWidth - StartMenuMetrics.listPadding * 2) / screenWidth
    val timerX = if (landscape) menuEnd + TimelineMetrics.fabInset else screenWidth * (1f - StartMenuMetrics.widthFraction) / 2f + TimelineMetrics.fabInset
    val searchesContacts = selected == StartMenuTab.Contacts
    val sheet = remember { Animatable(0f) }
    var resizing by remember { mutableStateOf(false) }
    val dragged = swipe.progress
    LaunchedEffect(open, dragged) {
        if (dragged != null) {
            resizing = true
            sheet.snapTo(dragged)
            return@LaunchedEffect
        }
        sheet.animateTo(if (open) 1f else 0f, tween(InfMotion.durPushMs, easing = InfMotion.easeIos))
        if (!open) resizing = false
    }
    val menuHeightPx = with(density) { menuHeight.toPx() }
    SideEffect { swipe.travel = menuHeightPx }
    val grown = if (resizing) sheet.value else 1f
    val searchReveal = if (resizing) {
        ((menuHeight * grown - StartMenuMetrics.searchTop - DsMetrics.searchHeight) / DsMetrics.searchHeight).coerceIn(0f, 1f)
    } else {
        1f
    }
    val timerShown = open && selected == StartMenuTab.Calendar && credentials.configured(calendarTracker)
    val searchShown = open && (selected == StartMenuTab.Apps || (searchesContacts && contactsPermission != PermissionState.Missing))

    StartTabsWindow(
        open = open,
        offsetY = if (landscape) tabsBottom else tabsOffsetY,
        tabs = tabs,
        selected = selected,
        onSelect = { selectedName = it.name },
        onClose = onClose,
        offsetX = if (landscape) (menuStart - menuEnd) / 2f else 0.dp,
        alpha = grown
    )
    StartMenuWindow(
        open = open,
        progress = { sheet.value },
        resizing = resizing,
        offsetY = baseBottom,
        height = menuHeight,
        onClose = onClose,
        widthFraction = menuFraction,
        offsetX = if (landscape) menuEnd else 0.dp,
        fromEnd = landscape
    ) {
        when (selected) {
            StartMenuTab.Apps -> AppsTab(open = open, onClose = onClose, onLaunched = onAppLaunched, onScrub = { scrubLetter = it }, viewModel = appsViewModel)
            StartMenuTab.Calendar -> CalendarTab(
                open = open,
                onClose = onClose,
                timeTracking = timeTracking,
                placement = HeaderPlacement(
                    top = menuTop + StartMenuMetrics.listTopPlain,
                    widthFraction = searchFraction,
                    fromEnd = landscape,
                    offsetX = if (landscape) menuEnd + StartMenuMetrics.listPadding else 0.dp
                )
            )
            StartMenuTab.Contacts -> ContactsTab(open = open, onClose = onClose, onScrub = { scrubLetter = it }, viewModel = contactsViewModel)
            StartMenuTab.Settings -> SettingsTab(
                settings = settings,
                onTabShown = viewModel::setTabShown,
                credentials = credentials,
                onCredentials = timeTracking::updateCredentials
            )
        }
    }
    TimerButtonWindow(
        visible = timerShown,
        running = running[calendarTracker],
        offsetX = timerX,
        offsetY = effectiveBottom + TimelineMetrics.fabInset,
        onStart = { startFor = calendarTracker },
        onStop = timeTracking::stop
    )
    StartSearchWindow(
        visible = searchShown,
        offsetY = searchOffset - menuHeight * (1f - grown),
        alpha = searchReveal,
        widthFraction = searchFraction,
        value = if (searchesContacts) contactsQuery else appsQuery,
        onValueChange = if (searchesContacts) contactsViewModel::setQuery else appsViewModel::setQuery,
        placeholder = stringResource(if (searchesContacts) R.string.startmenu_search_contacts else R.string.startmenu_search),
        clearDescription = stringResource(R.string.startmenu_clear),
        onClose = onClose,
        fromEnd = landscape,
        offsetX = if (landscape) menuEnd + StartMenuMetrics.listPadding else 0.dp
    )
    LetterBubbleWindow(letter = scrubLetter.takeIf { open }, offsetY = bubbleOffset)
    StartTimerSheet(
        tracker = startFor,
        loadProjects = timeTracking::projects,
        loadActivities = timeTracking::activities,
        onStart = timeTracking::start,
        onDismiss = { startFor = null }
    )
}
