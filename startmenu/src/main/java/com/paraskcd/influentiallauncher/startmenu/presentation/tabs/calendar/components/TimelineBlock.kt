// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics

@Composable
fun TimelineBlock(
    title: String,
    subtitle: String?,
    time: String?,
    colour: Color,
    highlighted: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val shape = RoundedCornerShape(TimelineMetrics.blockRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(colour.copy(alpha = TimelineMetrics.blockFillAlpha))
            .then(
                if (highlighted) {
                    Modifier.border(DsMetrics.hairlineThickness, colour.copy(alpha = TimelineMetrics.blockBorderAlpha), shape)
                } else {
                    Modifier
                }
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(TimelineMetrics.blockBarWidth)
                    .fillMaxHeight()
                    .background(colour)
            )
            Column(modifier = Modifier.padding(TimelineMetrics.blockPadding)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (time != null) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
