package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSegmented
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
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

    Column(
        verticalArrangement = Arrangement.spacedBy(TimelineMetrics.sectionGap),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = StartMenuMetrics.listTopPlain)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TimelineMetrics.sectionGap),
            modifier = Modifier.padding(horizontal = StartMenuMetrics.listPadding)
        ) {
            DateBar(
                date = date,
                caption = caption,
                onPrevious = viewModel::previousDay,
                onNext = viewModel::nextDay,
                onPick = { picking = date }
            )
            InfSegmented(
                labels = trackers.map { stringResource(TrackerLabels.nameOf(it)) },
                selected = trackers.indexOf(tracker),
                onSelect = { timeTracking.selectTracker(trackers[it]) },
                blurred = LocalWindowBlurred.current,
                modifier = Modifier.fillMaxWidth(),
                equalWidth = true
            )
            when {
                permission == PermissionState.Missing -> Text(
                    text = stringResource(R.string.startmenu_calendar_allow),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.brandText,
                    modifier = Modifier
                        .infGlassSurface(InfShapes.pill, specular = false, strong = true)
                        .clickable { calendarLauncher.launch(viewModel.permission) }
                        .padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s2)
                )
                allDay.isNotEmpty() -> AllDayStrip(
                    events = allDay,
                    onOpen = {
                        onClose()
                        viewModel.open(it)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DsMetrics.hairlineThickness)
                .background(colors.hairline)
        )
        if (notice != null) {
            Text(
                text = notice,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                modifier = Modifier
                    .padding(horizontal = StartMenuMetrics.listPadding)
                    .fillMaxWidth()
                    .infGlassSurface(InfShapes.md, specular = false, strong = true)
                    .padding(InfSpacing.s3)
            )
        }
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
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = StartMenuMetrics.listPadding)
        )
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
