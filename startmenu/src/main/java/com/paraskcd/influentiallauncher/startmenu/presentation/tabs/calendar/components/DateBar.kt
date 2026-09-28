// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DateBar(
    date: LocalDate,
    caption: String?,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val label = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(TimelineMetrics.barHeight)
            .infPanelSurface(InfShapes.pill, blurred = LocalWindowBlurred.current)
    ) {
        DayButton(Lucide.ChevronLeft, stringResource(R.string.startmenu_previous_day), onPrevious)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(onClickLabel = stringResource(R.string.startmenu_pick_date), onClick = onPick)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, maxLines = 1)
            if (caption != null) {
                Text(text = caption, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, maxLines = 1)
            }
        }
        DayButton(Lucide.ChevronRight, stringResource(R.string.startmenu_next_day), onNext)
    }
}

@Composable
private fun DayButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(TimelineMetrics.barHeight)
            .clip(CircleShape)
            .clickable(onClickLabel = description, onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = InfTheme.colors.textPrimary, modifier = Modifier.size(DsMetrics.iconButtonGlyph))
    }
}
