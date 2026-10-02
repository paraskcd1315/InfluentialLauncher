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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSwitch
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.timetracking.domain.model.TrackerCredentials
import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TabToggles
import java.text.DecimalFormat

@Composable
fun SettingsTab(
    settings: LauncherSettings,
    onTabShown: (StartMenuTab, Boolean) -> Unit,
    credentials: TrackerCredentials,
    schedule: WorkSchedule?,
    onCredentials: ((TrackerCredentials) -> TrackerCredentials) -> Unit,
    modifier: Modifier = Modifier
) {
    val toggles = TabToggles.of(settings)
    var togglToken by remember { mutableStateOf(credentials.togglToken) }
    var kimaiUrl by remember { mutableStateOf(credentials.kimaiUrl) }
    var kimaiToken by remember { mutableStateOf(credentials.kimaiToken) }
    var workSchedule by remember { mutableStateOf(credentials.workSchedule) }
    LaunchedEffect(credentials) {
        if (togglToken.isEmpty()) togglToken = credentials.togglToken
        if (kimaiUrl.isEmpty()) kimaiUrl = credentials.kimaiUrl
        if (kimaiToken.isEmpty()) kimaiToken = credentials.kimaiToken
        if (workSchedule.isEmpty()) workSchedule = credentials.workSchedule
    }
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
        item {
            InfTextField(
                value = workSchedule,
                onValueChange = { value ->
                    workSchedule = value
                    onCredentials { it.copy(workSchedule = value.trim()) }
                },
                label = if (schedule == null) {
                    stringResource(R.string.startmenu_settings_schedule)
                } else {
                    stringResource(
                        R.string.startmenu_settings_schedule_read,
                        DecimalFormat(HoursPattern).format(schedule.weeklyHours),
                        schedule.holidays.size
                    )
                },
                placeholder = ScheduleHint
            )
        }
    }
}

private const val KimaiUrlHint = "https://kimai.example.com"
private const val ScheduleHint = "{\"weeklyHours\":40,\"holidays\":[{\"date\":\"2026-12-25\"}]}"
private const val HoursPattern = "0.##"
