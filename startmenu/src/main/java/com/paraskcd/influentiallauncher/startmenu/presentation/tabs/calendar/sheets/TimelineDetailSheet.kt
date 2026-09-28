// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.organisms.InfAction
import com.paraskcd.influentiallauncher.designsystem.organisms.InfActionList
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.TimelineDetail
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TrackerLabels
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TimelineDetailSheet(
    detail: TimelineDetail?,
    onOpenEvent: (TimelineDetail.Event) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = InfTheme.colors
    val untitled = stringResource(R.string.startmenu_detail_untitled)
    InfSheetWindow(
        item = detail,
        title = { current ->
            when (current) {
                is TimelineDetail.Entry -> current.entry.description.ifBlank { current.entry.projectName ?: untitled }
                is TimelineDetail.Event -> current.event.title.ifBlank { untitled }
            }
        },
        onDismiss = onDismiss,
        leading = { current ->
            val colour = when (current) {
                is TimelineDetail.Entry -> current.entry.colourArgb?.let(::Color) ?: colors.brand
                is TimelineDetail.Event -> current.event.colorArgb?.let(::Color) ?: colors.brandText
            }
            Box(modifier = Modifier.size(TimelineMetrics.detailDot).clip(CircleShape).background(colour))
        }
    ) { current ->
        when (current) {
            is TimelineDetail.Entry -> EntryDetails(current)
            is TimelineDetail.Event -> {
                EventDetails(current)
                InfActionList(
                    actions = listOf(
                        InfAction(Lucide.Calendar, stringResource(R.string.startmenu_detail_open_event), colors.textPrimary) { onOpenEvent(current) }
                    ),
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
private fun EntryDetails(detail: TimelineDetail.Entry) {
    val entry = detail.entry
    val end = entry.end
    val minutes = Duration.between(entry.start, end ?: Instant.now()).toMinutes().coerceAtLeast(0)
    val running = stringResource(R.string.startmenu_detail_running)
    DetailCard(
        rows = listOfNotNull(
            entry.description.takeIf { it.isNotBlank() }?.let { stringResource(R.string.startmenu_detail_description) to it },
            entry.projectName?.let { stringResource(R.string.startmenu_timer_project) to it },
            entry.clientName?.let { stringResource(R.string.startmenu_detail_client) to it },
            entry.activityName?.takeIf { it != entry.description }?.let { stringResource(R.string.startmenu_timer_activity) to it },
            entry.tags.takeIf { it.isNotEmpty() }?.let { stringResource(R.string.startmenu_detail_tags) to it.joinToString(", ") },
            stringResource(R.string.startmenu_detail_time) to timeRange(entry.start, end, running),
            stringResource(R.string.startmenu_detail_duration) to stringResource(R.string.startmenu_total, minutes / MinutesPerHour, minutes % MinutesPerHour),
            stringResource(R.string.startmenu_detail_tracker) to stringResource(TrackerLabels.nameOf(entry.tracker))
        )
    )
}

@Composable
private fun EventDetails(detail: TimelineDetail.Event) {
    val event = detail.event
    val minutes = Duration.between(event.begin, event.end).toMinutes().coerceAtLeast(0)
    DetailCard(
        rows = listOfNotNull(
            stringResource(R.string.startmenu_detail_time) to timeRange(event.begin, event.end, ""),
            stringResource(R.string.startmenu_detail_duration) to stringResource(R.string.startmenu_total, minutes / MinutesPerHour, minutes % MinutesPerHour),
            event.location?.let { stringResource(R.string.startmenu_detail_location) to it },
            event.calendarName?.let { stringResource(R.string.startmenu_detail_calendar) to it },
            event.description?.let { stringResource(R.string.startmenu_detail_notes) to it }
        )
    )
}

@Composable
private fun DetailCard(rows: List<Pair<String, String>>) {
    val colors = InfTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap)) {
        rows.forEachIndexed { index, (label, value) ->
            InfGroupedCard(index = index, count = rows.size) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s3)) {
                    Text(text = label, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
                    Text(text = value, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
                }
            }
        }
    }
}

@Composable
private fun timeRange(start: Instant, end: Instant?, running: String): String {
    val zone = ZoneId.systemDefault()
    val format = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    val from = start.atZone(zone).toLocalTime().format(format)
    val to = end?.atZone(zone)?.toLocalTime()?.format(format) ?: running
    return stringResource(R.string.startmenu_event_time, from, to)
}

private const val MinutesPerHour = 60L
