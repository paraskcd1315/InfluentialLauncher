// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSegmented
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfHaze
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import com.paraskcd.influentiallauncher.designsystem.foundation.rememberInfHazeArea
import com.paraskcd.influentiallauncher.designsystem.foundation.infHazeSource
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.model.TimelineDetail
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.sheets.TimelineDetailSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.AllDayStrip
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.DateBar
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.DayTimeline
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.sheets.DatePickerSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TrackerLabels
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.CalendarViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.TimeTrackingViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.model.TrackerWeek
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerTotals
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CalendarTab(
    open: Boolean,
    onClose: () -> Unit,
    timeTracking: TimeTrackingViewModel,
    headerSwipe: SwipeUp?,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val colors = InfTheme.colors
    val permission by viewModel.permissionState.collectAsStateWithLifecycle()
    val date by viewModel.day.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val tracker by timeTracking.tracker.collectAsStateWithLifecycle()
    val credentials by timeTracking.credentials.collectAsStateWithLifecycle()
    val trackerDay by timeTracking.day.collectAsStateWithLifecycle()
    var picking by remember { mutableStateOf<LocalDate?>(null) }
    var detail by remember { mutableStateOf<TimelineDetail?>(null) }
    val calendarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.refreshPermission() }

    LaunchedEffect(open) {
        if (open) viewModel.refreshPermission() else viewModel.today()
        if (!open) detail = null
    }
    val trackersShown = timeTracking.available
    val trackers by timeTracking.shown.collectAsStateWithLifecycle()
    LaunchedEffect(open) {
        if (!open || !trackersShown) return@LaunchedEffect
        timeTracking.forgetPick()
        timeTracking.probe()
    }
    LaunchedEffect(open, date, trackers) {
        if (open && trackersShown) timeTracking.autoSelect(date)
    }
    LaunchedEffect(trackers, tracker) {
        timeTracking.keepShown(trackers)
    }
    LaunchedEffect(open, tracker, date, credentials) {
        if (open && trackersShown) timeTracking.load(date)
    }

    val current = trackerDay?.takeIf { it.tracker == tracker && it.date == date }
    val entries = current?.entries.orEmpty()
    val dayEvents = events.orEmpty()
    val allDay = dayEvents.filter { it.allDay }
    val trackerWeek by timeTracking.week.collectAsStateWithLifecycle()
    val caption = if (!trackersShown) null else totalCaption(
        durations = entries.map { Duration.between(it.start, it.end ?: Instant.now()) },
        today = date == LocalDate.now(),
        week = trackerWeek?.takeIf { it.tracker == tracker }
    )
    val trackerName = stringResource(TrackerLabels.nameOf(tracker))
    val notice = when {
        !trackersShown -> null
        !credentials.configured(tracker) -> stringResource(R.string.startmenu_tracker_setup, trackerName)
        current?.failed == true -> stringResource(R.string.startmenu_tracker_failed, trackerName)
        else -> null
    }

    var headerHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val gap = TimelineMetrics.sectionGap
    val stripShown = permission == PermissionState.Missing || allDay.isNotEmpty()
    val switchShown = trackers.size > 1
    val headerBottom = StartMenuMetrics.listTopPlain + headerHeight
    val haze = rememberInfHazeArea()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().infHazeSource(haze)) {
        DayTimeline(
            date = date,
            entries = entries,
            events = dayEvents.filterNot { it.allDay },
            onMove = timeTracking::move,
            onOpenEvent = { detail = TimelineDetail.Event(it) },
            onOpenEntry = { detail = TimelineDetail.Entry(it) },
            onDay = { viewModel.setDay(date.plusDays(it)) },
            topPadding = headerBottom + gap,
            showsTracker = trackersShown,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = StartMenuMetrics.listPadding)
        )
        if (notice != null) {
            Text(
                text = notice,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier
                    .padding(top = headerBottom + gap, start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding)
                    .fillMaxWidth()
                    .infGlassSurface(InfShapes.md, specular = false, strong = true)
                    .padding(InfSpacing.s3)
            )
        }
        }
        CompositionLocalProvider(LocalInfHaze provides haze) {
            Column(
                verticalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = StartMenuMetrics.listTopPlain, start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding)
                    .onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
                    .infSwipeUp(headerSwipe)
            ) {
                DateBar(
                    date = date,
                    caption = caption,
                    onPrevious = viewModel::previousDay,
                    onNext = viewModel::nextDay,
                    onPick = { picking = date }
                )
                if (switchShown) {
                    InfSegmented(
                        labels = trackers.map { stringResource(TrackerLabels.nameOf(it)) },
                        selected = trackers.indexOf(tracker),
                        onSelect = { timeTracking.selectTracker(trackers[it]) },
                        blurred = LocalWindowBlurred.current,
                        modifier = Modifier.fillMaxWidth(),
                        equalWidth = true
                    )
                }
                if (stripShown && permission == PermissionState.Missing) {
                    Text(
                        text = stringResource(R.string.startmenu_calendar_allow),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.brandText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .infPanelSurface(InfShapes.pill, blurred = LocalWindowBlurred.current)
                            .clickable { calendarLauncher.launch(viewModel.permission) }
                            .padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s3)
                    )
                } else if (stripShown) {
                    AllDayStrip(
                        events = allDay,
                        onOpen = { detail = TimelineDetail.Event(it) }
                    )
                }
            }
        }
    }

    DatePickerSheet(date = picking, onPick = viewModel::setDay, onDismiss = { picking = null })
    TimelineDetailSheet(
        detail = detail,
        onOpenEvent = {
            onClose()
            viewModel.open(it.event)
        },
        onDismiss = { detail = null }
    )
}

@Composable
private fun totalCaption(durations: List<Duration>, today: Boolean, week: TrackerWeek?): String? {
    val minutes = durations.sumOf { it.toMinutes() }
    if (minutes <= 0 && !today) return null
    val total = hoursAndMinutes(Duration.ofMinutes(minutes))
    if (!today) return total
    if (week == null) return stringResource(R.string.startmenu_today_total, total)
    val worked = hoursAndMinutes(TrackerTotals.of(week.entries, null, Instant.now(), ZoneId.systemDefault()).week)
    val weekly = week.target?.let { stringResource(R.string.startmenu_week_of, worked, hoursAndMinutes(it)) } ?: worked
    return stringResource(R.string.startmenu_today_week_total, total, weekly)
}

@Composable
private fun hoursAndMinutes(duration: Duration): String =
    stringResource(R.string.startmenu_total, duration.toHours(), duration.toMinutesPart())
