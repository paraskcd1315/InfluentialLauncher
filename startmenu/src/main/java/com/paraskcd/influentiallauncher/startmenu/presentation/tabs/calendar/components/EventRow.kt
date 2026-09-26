package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.calendar.domain.model.CalendarEvent
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.EventTimes
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics

@Composable
fun EventRow(
    event: CalendarEvent,
    index: Int,
    count: Int,
    onOpen: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val locale = LocalConfiguration.current.locales[0]
    val is24 = DateFormat.is24HourFormat(LocalContext.current)
    val time = if (event.allDay) {
        stringResource(R.string.startmenu_all_day)
    } else {
        stringResource(R.string.startmenu_event_time, EventTimes.time(event.begin, locale, is24), EventTimes.time(event.end, locale, is24))
    }
    InfGroupedCard(index = index, count = count, modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowIconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = event.title) { onOpen(event) }
                .padding(StartMenuMetrics.rowPadding)
        ) {
            Box(
                modifier = Modifier
                    .size(StartMenuMetrics.eventDotSize)
                    .clip(CircleShape)
                    .background(event.colorArgb?.let { Color(it) } ?: colors.brand)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = time, style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary, maxLines = 1)
                event.location?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
