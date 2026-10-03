// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSwitch
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.AccentSection
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.ShellAccessSection
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsActionRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsRemovableRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets.DaysOffSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets.MonthHoursSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.DayOffRuns
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ScheduleText
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TabToggles
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule
import java.time.LocalDate

@Composable
fun SettingsTab(
    settings: LauncherSettings,
    onTabShown: (StartMenuTab, Boolean) -> Unit,
    credentials: TrackerCredentials,
    schedule: WorkSchedule?,
    onCredentials: ((TrackerCredentials) -> TrackerCredentials) -> Unit,
    onSchedule: ((WorkSchedule) -> WorkSchedule) -> Unit,
    shellState: ShellState,
    onStartPairing: () -> Unit,
    onRetryShell: () -> Unit,
    onPickAccent: (String) -> Unit,
    onAccentFromWallpaper: () -> Unit,
    modifier: Modifier = Modifier
) {
    val toggles = TabToggles.of(settings)
    val held = schedule ?: WorkSchedule()
    var togglToken by remember { mutableStateOf(credentials.togglToken) }
    var kimaiUrl by remember { mutableStateOf(credentials.kimaiUrl) }
    var kimaiToken by remember { mutableStateOf(credentials.kimaiToken) }
    var weeklyHours by remember { mutableStateOf(ScheduleText.hours(held.weeklyHours)) }
    var workdays by remember { mutableStateOf(held.workdays.toString()) }
    var addingDays by remember { mutableStateOf(false) }
    var addingMonth by remember { mutableStateOf(false) }
    LaunchedEffect(credentials) {
        if (togglToken.isEmpty()) togglToken = credentials.togglToken
        if (kimaiUrl.isEmpty()) kimaiUrl = credentials.kimaiUrl
        if (kimaiToken.isEmpty()) kimaiToken = credentials.kimaiToken
    }
    LaunchedEffect(held.weeklyHours, held.workdays) {
        if (ScheduleText.hoursOf(weeklyHours) != held.weeklyHours) weeklyHours = ScheduleText.hours(held.weeklyHours)
        if (ScheduleText.workdaysOf(workdays) != held.workdays) workdays = held.workdays.toString()
    }
    val today = LocalDate.now()
    val months = held.hoursByMonth.toSortedMap().toList()
    val runs = DayOffRuns.of(held.holidays, held.workdays)
    val upcoming = runs.filter { !it.to.isBefore(today) }
    val earlier = runs.filter { it.to.isBefore(today) }.sumOf { it.days.size }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
        contentPadding = PaddingValues(
            start = StartMenuMetrics.listPadding,
            end = StartMenuMetrics.listPadding,
            top = StartMenuMetrics.listTopPlain,
            bottom = StartMenuMetrics.listBottom
        ),
        modifier = modifier.fillMaxSize()
    ) {
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_section)) }
        itemsIndexed(toggles, key = { _, toggle -> toggle.tab.name }) { index, toggle ->
            InfGroupedCard(index = index, count = toggles.size) {
                InfSettingsRow(
                    label = stringResource(toggle.labelRes),
                    caption = stringResource(R.string.startmenu_settings_caption),
                    trailing = { InfSwitch(checked = toggle.shown, onCheckedChange = { onTabShown(toggle.tab, it) }) }
                )
            }
        }
        item {
            ShellAccessSection(
                state = shellState,
                onStartPairing = onStartPairing,
                onRetry = onRetryShell
            )
        }
        item {
            AccentSection(
                onPick = onPickAccent,
                onWallpaper = onAccentFromWallpaper
            )
        }
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_tracking)) }
        item {
            InfTextField(
                value = togglToken,
                onValueChange = { value ->
                    togglToken = value
                    onCredentials { it.copy(togglToken = value.trim()) }
                },
                label = stringResource(R.string.startmenu_settings_toggl_token),
                secret = true
            )
        }
        item {
            InfTextField(
                value = kimaiUrl,
                onValueChange = { value ->
                    kimaiUrl = value
                    onCredentials { it.copy(kimaiUrl = value.trim()) }
                },
                label = stringResource(R.string.startmenu_settings_kimai_url),
                placeholder = KimaiUrlHint,
                keyboardType = KeyboardType.Uri
            )
        }
        item {
            InfTextField(
                value = kimaiToken,
                onValueChange = { value ->
                    kimaiToken = value
                    onCredentials { it.copy(kimaiToken = value.trim()) }
                },
                label = stringResource(R.string.startmenu_settings_kimai_token),
                secret = true
            )
        }
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_schedule)) }
        item {
            InfTextField(
                value = weeklyHours,
                onValueChange = { value ->
                    weeklyHours = value
                    ScheduleText.hoursOf(value)?.let { hours -> onSchedule { it.copy(weeklyHours = hours) } }
                },
                label = stringResource(R.string.startmenu_settings_weekly_hours),
                keyboardType = KeyboardType.Decimal
            )
        }
        item {
            InfTextField(
                value = workdays,
                onValueChange = { value ->
                    workdays = value
                    ScheduleText.workdaysOf(value)?.let { days -> onSchedule { it.copy(workdays = days) } }
                },
                label = stringResource(R.string.startmenu_settings_workdays),
                keyboardType = KeyboardType.Number
            )
        }
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_months)) }
        itemsIndexed(months, key = { _, entry -> "month:${entry.first}" }) { index, (month, hours) ->
            SettingsRemovableRow(
                index = index,
                count = months.size + 1,
                label = ScheduleText.month(month),
                caption = stringResource(R.string.startmenu_settings_month_hours, ScheduleText.hours(hours)),
                onRemove = { onSchedule { it.copy(hoursByMonth = it.hoursByMonth - month) } }
            )
        }
        item {
            SettingsActionRow(
                index = months.size,
                count = months.size + 1,
                label = stringResource(R.string.startmenu_settings_add_month),
                onClick = { addingMonth = true }
            )
        }
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_days_off)) }
        itemsIndexed(upcoming, key = { _, run -> "off:${run.from}" }) { index, run ->
            SettingsRemovableRow(
                index = index,
                count = upcoming.size + 1,
                label = if (run.days.size == 1) {
                    ScheduleText.day(run.from)
                } else {
                    stringResource(R.string.startmenu_settings_run, ScheduleText.day(run.from), ScheduleText.day(run.to))
                },
                caption = pluralStringResource(R.plurals.startmenu_settings_days, run.days.size, run.days.size),
                onRemove = { onSchedule { it.copy(holidays = it.holidays - run.days.toSet()) } }
            )
        }
        item {
            SettingsActionRow(
                index = upcoming.size,
                count = upcoming.size + 1,
                label = stringResource(R.string.startmenu_settings_add_days_off),
                onClick = { addingDays = true }
            )
        }
        if (earlier > 0) {
            item {
                InfSettingsRow(label = pluralStringResource(R.plurals.startmenu_settings_earlier_days, earlier, earlier))
            }
        }
    }
    DaysOffSheet(
        open = addingDays,
        workdays = held.workdays,
        onAdd = { days -> onSchedule { it.copy(holidays = it.holidays + days) } },
        onDismiss = { addingDays = false }
    )
    MonthHoursSheet(
        open = addingMonth,
        weeklyHours = held.weeklyHours,
        taken = held.hoursByMonth.keys,
        onPick = { month, hours -> onSchedule { it.copy(hoursByMonth = it.hoursByMonth + (month to hours)) } },
        onDismiss = { addingMonth = false }
    )
}

private const val KimaiUrlHint = "https://kimai.example.com"
