package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun AllDayStrip(
    events: List<CalendarEvent>,
    onOpen: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        contentPadding = PaddingValues(InfSpacing.s1),
        modifier = modifier
            .fillMaxWidth()
            .infPanelSurface(InfShapes.pill, blurred = LocalWindowBlurred.current)
    ) {
        items(events, key = { it.id }) { event ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(TimelineMetrics.chipHeight)
                    .infGlassSurface(InfShapes.pill, specular = false)
                    .clickable(onClickLabel = event.title) { onOpen(event) }
                    .padding(horizontal = InfSpacing.s3)
            ) {
                Box(
                    modifier = Modifier
                        .size(TimelineMetrics.chipDot)
                        .clip(CircleShape)
                        .background(event.colorArgb?.let(::Color) ?: colors.brandText)
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
