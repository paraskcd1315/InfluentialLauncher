// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Square
import com.composables.icons.lucide.Timer
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.timetracking.domain.model.Tracker
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerTotals
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Composable
fun TimerGlance(
    tracker: Tracker,
    entry: TimeEntry?,
    week: List<TimeEntry>?,
    weekTarget: Duration?,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now by produceState(Instant.now(), entry?.id, entry?.start, week) {
        while (true) {
            value = Instant.now()
            delay(if (entry == null) GlanceMetrics.idleTickMs else GlanceMetrics.tickMs)
        }
    }
    val totals = week?.let { TrackerTotals.of(it, entry, now, ZoneId.systemDefault()) }
    val today = totals?.let { stringResource(R.string.glance_today, hoursAndMinutes(it.today)) }
    val weekly = totals?.let {
        if (weekTarget == null) {
            stringResource(R.string.glance_week, hoursAndMinutes(it.week))
        } else {
            stringResource(R.string.glance_week_of, hoursAndMinutes(it.week), hoursAndMinutes(weekTarget))
        }
    }
    val headline = entry?.let { clock(Duration.between(it.start, now)) } ?: today.orEmpty()
    val aside = if (entry == null) null else listOfNotNull(today, weekly).joinToString(Separator).takeIf { it.isNotEmpty() }
    val detail = if (entry == null) {
        listOfNotNull(weekly, tracker.name).joinToString(Separator)
    } else {
        val title = entry.description.ifBlank { entry.projectName ?: stringResource(R.string.glance_timer, tracker.name) }
        listOfNotNull(title, entry.projectName?.takeIf { it != title }).joinToString(Separator)
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Lucide.Timer,
            contentDescription = null,
            tint = entry?.colourArgb?.let(::Color) ?: LocalWallpaperInk.current.content,
            modifier = Modifier.size(GlanceMetrics.icon)
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap)) {
                GlanceText(text = headline, style = MaterialTheme.typography.titleLarge, modifier = Modifier.alignByBaseline())
                if (aside != null) {
                    GlanceText(
                        text = aside,
                        style = MaterialTheme.typography.bodyMedium,
                        alpha = GlanceMetrics.secondaryAlpha,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
            GlanceText(text = detail, style = MaterialTheme.typography.bodyMedium, alpha = GlanceMetrics.secondaryAlpha)
        }
        if (entry != null) GlanceControl(Lucide.Square, stringResource(R.string.glance_stop), onStop, tint = InfTheme.colors.danger)
    }
}

@Composable
private fun hoursAndMinutes(duration: Duration): String = if (duration.toMinutesPart() == 0 && duration.toHours() > 0) {
    stringResource(R.string.glance_hours, duration.toHours())
} else {
    stringResource(R.string.glance_hours_minutes, duration.toHours(), duration.toMinutesPart())
}

private fun clock(duration: Duration): String {
    val safe = if (duration.isNegative) Duration.ZERO else duration
    return "%d:%02d:%02d".format(safe.toHours(), safe.toMinutesPart(), safe.toSecondsPart())
}

private const val Separator = " · "
