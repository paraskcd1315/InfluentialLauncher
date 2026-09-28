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
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

@Composable
fun TimerGlance(entry: TimeEntry, onStop: () -> Unit, modifier: Modifier = Modifier) {
    val elapsed by produceState(Duration.between(entry.start, Instant.now()), entry.id, entry.start) {
        while (true) {
            value = Duration.between(entry.start, Instant.now())
            delay(GlanceMetrics.tickMs)
        }
    }
    val title = entry.description.ifBlank { entry.projectName ?: stringResource(R.string.glance_timer, entry.tracker.name) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(GlanceMetrics.itemGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Lucide.Timer,
            contentDescription = null,
            tint = entry.colourArgb?.let(::Color) ?: LocalWallpaperInk.current.content,
            modifier = Modifier.size(GlanceMetrics.icon)
        )
        Column(modifier = Modifier.weight(1f)) {
            GlanceText(text = format(elapsed), style = MaterialTheme.typography.titleLarge)
            GlanceText(
                text = listOfNotNull(title, entry.projectName?.takeIf { it != title }).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                alpha = GlanceMetrics.secondaryAlpha
            )
        }
        GlanceControl(Lucide.Square, stringResource(R.string.glance_stop), onStop, tint = InfTheme.colors.danger)
    }
}

private fun format(duration: Duration): String {
    val safe = if (duration.isNegative) Duration.ZERO else duration
    return "%d:%02d:%02d".format(safe.toHours(), safe.toMinutesPart(), safe.toSecondsPart())
}
