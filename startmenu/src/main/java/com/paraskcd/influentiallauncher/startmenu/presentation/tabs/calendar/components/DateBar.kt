package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TimelineMetrics
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
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        InfIconButton(
            icon = Lucide.ChevronLeft,
            contentDescription = stringResource(R.string.startmenu_previous_day),
            tint = colors.textPrimary,
            onClick = onPrevious
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(TimelineMetrics.barHeight)
                .infGlassSurface(InfShapes.pill, specular = false, strong = true)
                .clickable(onClickLabel = stringResource(R.string.startmenu_pick_date), onClick = onPick)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, maxLines = 1)
            if (caption != null) {
                Text(text = caption, style = MaterialTheme.typography.labelSmall, color = colors.textSecondary, maxLines = 1)
            }
        }
        InfIconButton(
            icon = Lucide.ChevronRight,
            contentDescription = stringResource(R.string.startmenu_next_day),
            tint = colors.textPrimary,
            onClick = onNext
        )
    }
}
