package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.calendar.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.atoms.InfButton
import com.paraskcd.influentiallauncher.designsystem.organisms.InfDatePicker
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import java.time.LocalDate

@Composable
fun DatePickerSheet(
    date: LocalDate?,
    onPick: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    InfSheetWindow(
        item = date,
        title = { stringResource(R.string.startmenu_pick_date) },
        onDismiss = onDismiss
    ) { current ->
        InfDatePicker(
            selected = current,
            onSelect = {
                onPick(it)
                onDismiss()
            },
            previousYearDescription = stringResource(R.string.startmenu_previous_year),
            nextYearDescription = stringResource(R.string.startmenu_next_year)
        )
        InfButton(
            label = stringResource(R.string.startmenu_today),
            onClick = {
                onPick(LocalDate.now())
                onDismiss()
            }
        )
    }
}
