package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTile
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.EventTimes
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import java.time.LocalDate

@Composable
fun DayHeader(
    day: LocalDate,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val locale = LocalConfiguration.current.locales[0]
    val isToday = day == LocalDate.now()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = StartMenuMetrics.dayHeaderHeight)
    ) {
        InfTile(onClick = onPrevious, contentDescription = stringResource(R.string.startmenu_previous_day), shape = InfShapes.pill) {
            Icon(Lucide.ChevronLeft, contentDescription = stringResource(R.string.startmenu_previous_day), tint = colors.textPrimary, modifier = Modifier.size(DsMetrics.glyphSize))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onToday)
        ) {
            Text(
                text = EventTimes.day(day, locale),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
                maxLines = 1
            )
            if (isToday) {
                Text(text = stringResource(R.string.startmenu_today), style = MaterialTheme.typography.labelMedium, color = colors.brandText)
            }
        }
        InfTile(onClick = onNext, contentDescription = stringResource(R.string.startmenu_next_day), shape = InfShapes.pill) {
            Icon(Lucide.ChevronRight, contentDescription = stringResource(R.string.startmenu_next_day), tint = colors.textPrimary, modifier = Modifier.size(DsMetrics.glyphSize))
        }
    }
}
