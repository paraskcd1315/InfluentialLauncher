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
import com.paraskcd.influentiallauncher.startmenu.presentation.model.HeaderPlacement
import com.paraskcd.influentiallauncher.startmenu.presentation.model.PermissionState
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.AllDayStrip
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.DateBar
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components.DayTimeline
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.sheets.DatePickerSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TrackerLabels
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.CalendarViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.viewmodels.TimeTrackingViewModel
import com.paraskcd.influentiallauncher.startmenu.presentation.windows.HeaderWindow
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

@Composable
fun CalendarTab(
    open: Boolean,
    onClose: () -> Unit,
    timeTracking: TimeTrackingViewModel,
    placement: HeaderPlacement,
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
    val calendarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { viewModel.refreshPermission() }

    LaunchedEffect(open) {
        if (open) viewModel.refreshPermission() else viewModel.today()
    }
    LaunchedEffect(open, tracker, date, credentials) {
        if (open) timeTracking.load(date)
    }

    val trackers = Tracker.entries
    val current = trackerDay?.takeIf { it.tracker == tracker && it.date == date }
    val entries = current?.entries.orEmpty()
    val dayEvents = events.orEmpty()
    val allDay = dayEvents.filter { it.allDay }
    val caption = totalCaption(entries.map { Duration.between(it.start, it.end ?: Instant.now()) }, date == LocalDate.now())
    val trackerName = stringResource(TrackerLabels.nameOf(tracker))
    val notice = when {
        !credentials.configured(tracker) -> stringResource(R.string.startmenu_tracker_setup, trackerName)
        current?.failed == true -> stringResource(R.string.startmenu_tracker_failed, trackerName)
        else -> null
    }

    var dateHeight by remember { mutableStateOf(0.dp) }
    var trackerHeight by remember { mutableStateOf(0.dp) }
    var stripHeight by remember { mutableStateOf(0.dp) }
    val gap = TimelineMetrics.sectionGap
    val stripShown = permission == PermissionState.Missing || allDay.isNotEmpty()
    val trackerTop = dateHeight + gap
    val stripTop = trackerTop + trackerHeight + gap
    val headerBottom = StartMenuMetrics.listTopPlain + (if (stripShown) stripTop + stripHeight else trackerTop + trackerHeight)

    HeaderWindow(visible = open, placement = placement, offsetTop = 0.dp, onHeight = { dateHeight = it }, onClose = onClose) {
        DateBar(
            date = date,
            caption = caption,
            onPrevious = viewModel::previousDay,
            onNext = viewModel::nextDay,
            onPick = { picking = date }
        )
    }
    HeaderWindow(visible = open, placement = placement, offsetTop = trackerTop, onHeight = { trackerHeight = it }, onClose = onClose) {
        InfSegmented(
            labels = trackers.map { stringResource(TrackerLabels.nameOf(it)) },
            selected = trackers.indexOf(tracker),
            onSelect = { timeTracking.selectTracker(trackers[it]) },
            blurred = LocalWindowBlurred.current,
            modifier = Modifier.fillMaxWidth(),
            equalWidth = true
        )
    }
    HeaderWindow(visible = open && stripShown, placement = placement, offsetTop = stripTop, onHeight = { stripHeight = it }, onClose = onClose) {
        if (permission == PermissionState.Missing) {
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
        } else {
            AllDayStrip(
                events = allDay,
                onOpen = {
                    onClose()
                    viewModel.open(it)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DayTimeline(
            date = date,
            entries = entries,
            events = dayEvents.filterNot { it.allDay },
            onMove = timeTracking::move,
            onOpenEvent = {
                onClose()
                viewModel.open(it)
            },
            onDay = { viewModel.setDay(date.plusDays(it)) },
            topPadding = headerBottom + gap,
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

    DatePickerSheet(date = picking, onPick = viewModel::setDay, onDismiss = { picking = null })
}

@Composable
private fun totalCaption(durations: List<Duration>, today: Boolean): String? {
    val minutes = durations.sumOf { it.toMinutes() }
    if (minutes <= 0 && !today) return null
    val total = stringResource(R.string.startmenu_total, minutes / MinutesPerHour, minutes % MinutesPerHour)
    return if (today) stringResource(R.string.startmenu_today_total, total) else total
}

private const val MinutesPerHour = 60L
