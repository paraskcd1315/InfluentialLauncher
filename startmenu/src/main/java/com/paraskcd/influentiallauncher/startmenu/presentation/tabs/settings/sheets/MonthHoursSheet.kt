// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ScheduleText
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import java.time.Month
import java.time.format.TextStyle

@Composable
fun MonthHoursSheet(
    open: Boolean,
    weeklyHours: Double,
    taken: Set<Int>,
    onPick: (Int, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var hours by remember(open, weeklyHours) { mutableStateOf(ScheduleText.hours(weeklyHours)) }
    InfSheetWindow(
        item = if (open) Unit else null,
        title = { stringResource(R.string.startmenu_settings_month_title) },
        onDismiss = onDismiss
    ) {
        val parsed = ScheduleText.hoursOf(hours)
        InfTextField(
            value = hours,
            onValueChange = { hours = it },
            label = stringResource(R.string.startmenu_settings_month_hours_label),
            keyboardType = KeyboardType.Decimal
        )
        Month.entries.chunked(MonthsPerRow).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2), modifier = Modifier.fillMaxWidth()) {
                row.forEach { month ->
                    InfButton(
                        label = ScheduleText.month(month.value, TextStyle.SHORT_STANDALONE),
                        onClick = {
                            if (parsed != null) {
                                onPick(month.value, parsed)
                                onDismiss()
                            }
                        },
                        enabled = parsed != null && month.value !in taken,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private const val MonthsPerRow = 4
