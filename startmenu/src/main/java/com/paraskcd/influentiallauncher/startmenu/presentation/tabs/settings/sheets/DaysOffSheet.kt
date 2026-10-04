// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.organisms.InfDatePicker
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.DayOffRuns
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ScheduleText
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun DaysOffSheet(
    open: Boolean,
    weeklyDaysOff: Set<DayOfWeek>,
    onAdd: (List<LocalDate>) -> Unit,
    onDismiss: () -> Unit
) {
    var first by remember { mutableStateOf<LocalDate?>(null) }
    LaunchedEffect(open) { if (!open) first = null }
    InfSheetWindow(
        item = if (open) Unit else null,
        title = { stringResource(if (first == null) R.string.startmenu_settings_first_day else R.string.startmenu_settings_last_day) },
        onDismiss = onDismiss
    ) {
        val start = first
        InfDatePicker(
            selected = start ?: LocalDate.now(),
            onSelect = { day ->
                if (start == null) {
                    first = day
                } else {
                    onAdd(DayOffRuns.between(minOf(start, day), maxOf(start, day), weeklyDaysOff))
                    onDismiss()
                }
            },
            previousYearDescription = stringResource(R.string.startmenu_previous_year),
            nextYearDescription = stringResource(R.string.startmenu_next_year)
        )
        if (start != null) {
            InfButton(
                label = stringResource(R.string.startmenu_settings_only_day, ScheduleText.day(start)),
                onClick = {
                    onAdd(listOf(start))
                    onDismiss()
                }
            )
        }
    }
}
