// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.windows

import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Play
import com.composables.icons.lucide.Square
import com.paraskcd.influentiallauncher.designsystem.foundation.infAccentGradientTint
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.timetracking.domain.model.TimeEntry
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

@Composable
fun TimerButtonWindow(
    visible: Boolean,
    running: TimeEntry?,
    offsetX: Dp,
    offsetY: Dp,
    onStart: () -> Unit,
    onStop: (TimeEntry) -> Unit
) {
    InfWindow(
        cornerRadius = InfRadii.pill,
        onDismissRequest = {},
        gravity = Gravity.BOTTOM or Gravity.END,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = visible
    ) {
        val surface = Modifier.infPanelSurface(InfShapes.pill, blurred = LocalWindowBlurred.current)
        if (running == null) {
            val label = stringResource(R.string.startmenu_timer_start)
            Box(
                contentAlignment = Alignment.Center,
                modifier = surface
                    .size(TimelineMetrics.fabSize)
                    .clickable(onClickLabel = label, onClick = onStart)
            ) {
                Icon(
                    imageVector = Lucide.Play,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier
                        .infParallaxLayer()
                        .size(TimelineMetrics.fabGlyph)
                        .infAccentGradientTint()
                )
            }
        } else {
            RunningTimer(entry = running, onStop = onStop, modifier = surface)
        }
    }
}

@Composable
private fun RunningTimer(entry: TimeEntry, onStop: (TimeEntry) -> Unit, modifier: Modifier) {
    val colors = InfTheme.colors
    val label = stringResource(R.string.startmenu_timer_stop)
    val elapsed by produceState(Duration.between(entry.start, Instant.now()), entry.id, entry.start) {
        while (true) {
            value = Duration.between(entry.start, Instant.now())
            delay(TickMs)
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(TimelineMetrics.fabSize)
            .clickable(onClickLabel = label) { onStop(entry) }
            .padding(horizontal = InfSpacing.s4)
    ) {
        Icon(
            imageVector = Lucide.Square,
            contentDescription = label,
            tint = colors.danger,
            modifier = Modifier.infParallaxLayer().size(TimelineMetrics.fabGlyph)
        )
        Text(
            text = format(elapsed),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            modifier = Modifier.infParallaxLayer()
        )
        val title = entry.description.ifBlank { entry.projectName.orEmpty() }
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.infParallaxLayer().widthIn(max = TimelineMetrics.fabLabelMax)
            )
        }
    }
}

private fun format(duration: Duration): String {
    val safe = if (duration.isNegative) Duration.ZERO else duration
    return "%d:%02d:%02d".format(safe.toHours(), safe.toMinutesPart(), safe.toSecondsPart())
}

private const val TickMs = 1_000L
