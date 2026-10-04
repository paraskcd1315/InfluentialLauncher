// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings

import androidx.activity.compose.PredictiveBackHandler
import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.composables.icons.lucide.ExternalLink
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.AboutHero
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.OtherAppRow
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AboutLinks
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.OtherApps
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsMetrics
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import com.paraskcd.influentiallauncher.apps.domain.model.IconPack
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSwitch
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSettingsRow
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.settings.domain.model.LauncherSettings
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.startmenu.R
import com.paraskcd.influentiallauncher.startmenu.presentation.model.AppearanceToggle
import com.paraskcd.influentiallauncher.startmenu.presentation.model.SettingsSection
import com.paraskcd.influentiallauncher.startmenu.presentation.model.StartMenuTab
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.AccentSection
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsActionRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsCrumb
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsRemovableRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.SettingsSectionRow
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.components.ShellAccessSection
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets.DaysOffSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets.IconPackSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.tabs.settings.sheets.MonthHoursSheet
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.AppearanceToggles
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.DayOffRuns
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.ScheduleText
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.SettingsSections
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.StartMenuMetrics
import com.paraskcd.influentiallauncher.startmenu.presentation.utils.TabToggles
import com.paraskcd.influentiallauncher.timetracking.domain.model.WorkSchedule
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun SettingsTab(
    settings: LauncherSettings,
    onTabShown: (StartMenuTab, Boolean) -> Unit,
    onSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    iconPack: String?,
    packs: List<IconPack>?,
    onLoadPacks: () -> Unit,
    onIconPack: (String?) -> Unit,
    trackersAvailable: Boolean,
    schedule: WorkSchedule?,
    onSchedule: ((WorkSchedule) -> WorkSchedule) -> Unit,
    shellState: ShellState,
    onStartPairing: () -> Unit,
    onRetryShell: () -> Unit,
    onPickAccent: (String) -> Unit,
    onAccentFromWallpaper: () -> Unit,
    modifier: Modifier = Modifier
) {
    var opened by rememberSaveable { mutableStateOf<SettingsSection?>(null) }
    val context = LocalContext.current
    val openFailed = stringResource(R.string.startmenu_about_open_failed)
    val held = schedule ?: WorkSchedule()
    var addingDays by remember { mutableStateOf(false) }
    var addingMonth by remember { mutableStateOf(false) }
    var pickingPack by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { onLoadPacks() }
    val seek = remember { SeekableTransitionState(opened) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(opened) { if (seek.targetState != opened || seek.fraction != 0f) seek.animateTo(opened) }
    PredictiveBackHandler(enabled = opened != null) { progress ->
        try {
            progress.collect { event -> seek.seekTo(event.progress, targetState = null) }
            opened = null
        } catch (cancelled: CancellationException) {
            scope.launch { seek.animateTo(seek.currentState) }
            throw cancelled
        }
    }
    val push = tween<IntOffset>(InfMotion.durPushMs, easing = InfMotion.easeIos)
    rememberTransition(seek, label = "settingsSection").AnimatedContent(
        transitionSpec = {
            val forward = if (targetState != null) 1 else -1
            slideInHorizontally(push) { width -> width * forward } togetherWith slideOutHorizontally(push) { width -> -width * forward }
        },
        modifier = modifier.fillMaxSize()
    ) { section ->
        if (section == null) {
            val sections = SettingsSections.of(trackersAvailable)
            SettingsList {
                itemsIndexed(sections, key = { _, entry -> entry.name }) { index, entry ->
                    SettingsSectionRow(section = entry, index = index, count = sections.size, onOpen = { opened = entry })
                }
            }
            return@AnimatedContent
        }
        val listState = rememberLazyListState()
        val density = LocalDensity.current
        var headerHeight by remember { mutableStateOf(0.dp) }
        Box(modifier = Modifier.fillMaxSize()) {
            when (section) {
                SettingsSection.Appearance -> SettingsList(listState, headerHeight) {
                    item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_home_screen)) }
                    toggleRows(AppearanceToggles.homeScreen(settings), onSettings)
                    item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_section)) }
                    startMenuRows(settings, onTabShown, onSettings)
                    item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_icons)) }
                    iconRows(settings, onSettings, iconPack, packs, onPickPack = { pickingPack = true })
                    item { InfSectionHeader(text = stringResource(R.string.startmenu_accent_section)) }
                    item(key = "appearance:accent") { AccentSection(onPick = onPickAccent, onWallpaper = onAccentFromWallpaper) }
                    toggleRows(AppearanceToggles.colours(settings), onSettings)
                }
                SettingsSection.Shell -> SettingsList(listState, headerHeight) {
                    item { ShellAccessSection(state = shellState, onStartPairing = onStartPairing, onRetry = onRetryShell) }
                }
                SettingsSection.Schedule -> ScheduleSection(
                    listState = listState,
                    top = headerHeight,
                    held = held,
                    onSchedule = onSchedule,
                    onAddDays = { addingDays = true },
                    onAddMonth = { addingMonth = true }
                )
                SettingsSection.OtherApps -> SettingsList(listState, headerHeight) {
                    itemsIndexed(OtherApps.all, key = { _, app -> app.packageName }) { index, app ->
                        OtherAppRow(app = app, index = index, count = OtherApps.all.size, onOpen = { OtherApps.open(context, app) })
                    }
                }
                SettingsSection.About -> SettingsList(listState, headerHeight) {
                    aboutRows(onSource = {
                        if (!OtherApps.openLink(context, AboutLinks.SOURCE)) Toast.makeText(context, openFailed, Toast.LENGTH_SHORT).show()
                    })
                }
            }
            SettingsHeader(
                scrolled = listState.canScrollBackward,
                modifier = Modifier.onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
            ) {
                SettingsCrumb(
                    label = stringResource(section.titleRes),
                    onBack = { opened = null },
                    modifier = Modifier.padding(start = StartMenuMetrics.listPadding, end = StartMenuMetrics.listPadding, top = StartMenuMetrics.listTopPlain)
                )
            }
        }
    }
    IconPackSheet(
        open = pickingPack,
        packs = packs.orEmpty(),
        selected = iconPack,
        onPick = onIconPack,
        onDismiss = { pickingPack = false }
    )
    DaysOffSheet(
        open = addingDays,
        weeklyDaysOff = held.weeklyDaysOff,
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

@Composable
private fun SettingsList(
    state: LazyListState = rememberLazyListState(),
    top: Dp = 0.dp,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(StartMenuMetrics.rowGap),
        contentPadding = PaddingValues(
            start = StartMenuMetrics.listPadding,
            end = StartMenuMetrics.listPadding,
            top = top + StartMenuMetrics.listTopPlain,
            bottom = StartMenuMetrics.listBottom
        ),
        modifier = Modifier.fillMaxSize(),
        content = content
    )
}

@Composable
private fun SettingsHeader(scrolled: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val colors = InfTheme.colors
    val fill by animateFloatAsState(
        targetValue = if (scrolled) 1f else 0f,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "settingsHeader"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind { drawRect(colors.glassStrongBg.copy(alpha = colors.glassStrongBg.alpha * fill)) }
    ) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DsMetrics.hairlineThickness)
                .graphicsLayer { alpha = fill }
                .background(colors.hairline)
        )
    }
}

private fun LazyListScope.toggleRows(
    toggles: List<AppearanceToggle>,
    onSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    first: Int = 0,
    count: Int = toggles.size
) {
    itemsIndexed(toggles, key = { _, toggle -> "appearance:${toggle.key}" }) { index, toggle ->
        InfGroupedCard(index = first + index, count = count) {
            InfSettingsRow(
                label = stringResource(toggle.labelRes),
                trailing = { InfSwitch(checked = toggle.on, onCheckedChange = { on -> onSettings { toggle.apply(it, on) } }) }
            )
        }
    }
}

private fun LazyListScope.aboutRows(onSource: () -> Unit) {
    item(key = "about:hero") { AboutHero() }
    item { InfSectionHeader(text = stringResource(R.string.startmenu_about_links)) }
    item(key = "about:source") {
        InfGroupedCard(index = 0, count = 1) {
            InfSettingsRow(
                label = stringResource(R.string.startmenu_about_source),
                caption = stringResource(R.string.startmenu_about_source_caption),
                trailing = {
                    Icon(
                        imageVector = Lucide.ExternalLink,
                        contentDescription = null,
                        tint = InfTheme.colors.textTertiary,
                        modifier = Modifier.size(SettingsMetrics.chevron)
                    )
                },
                modifier = Modifier.clickable(onClick = onSource)
            )
        }
    }
    item(key = "about:developer") {
        Text(
            text = stringResource(R.string.startmenu_about_developer, stringResource(R.string.startmenu_about_developer_name)),
            style = MaterialTheme.typography.labelMedium,
            color = InfTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = SettingsMetrics.aboutPadding)
        )
    }
}

private fun LazyListScope.startMenuRows(
    settings: LauncherSettings,
    onTabShown: (StartMenuTab, Boolean) -> Unit,
    onSettings: ((LauncherSettings) -> LauncherSettings) -> Unit
) {
    val tabs = TabToggles.of(settings)
    val toggles = AppearanceToggles.startMenu(settings)
    val count = tabs.size + toggles.size
    itemsIndexed(tabs, key = { _, toggle -> toggle.tab.name }) { index, toggle ->
        InfGroupedCard(index = index, count = count) {
            InfSettingsRow(
                label = stringResource(toggle.labelRes),
                caption = stringResource(R.string.startmenu_settings_caption),
                trailing = { InfSwitch(checked = toggle.shown, onCheckedChange = { onTabShown(toggle.tab, it) }) }
            )
        }
    }
    toggleRows(toggles, onSettings, first = tabs.size, count = count)
}

private fun LazyListScope.iconRows(
    settings: LauncherSettings,
    onSettings: ((LauncherSettings) -> LauncherSettings) -> Unit,
    iconPack: String?,
    packs: List<IconPack>?,
    onPickPack: () -> Unit
) {
    val toggles = AppearanceToggles.icons(settings)
    val count = toggles.size + 1
    item(key = "appearance:iconPack") {
        val packLabel = packs?.firstOrNull { it.packageName == iconPack }?.label ?: stringResource(R.string.startmenu_settings_icon_pack_system)
        InfGroupedCard(index = 0, count = count) {
            InfSettingsRow(
                label = stringResource(R.string.startmenu_settings_icon_pack),
                caption = packLabel,
                modifier = Modifier.clickable(onClick = onPickPack)
            )
        }
    }
    toggleRows(toggles, onSettings, first = 1, count = count)
}

@Composable
private fun ScheduleSection(
    listState: LazyListState,
    top: Dp,
    held: WorkSchedule,
    onSchedule: ((WorkSchedule) -> WorkSchedule) -> Unit,
    onAddDays: () -> Unit,
    onAddMonth: () -> Unit
) {
    var weeklyHours by remember { mutableStateOf(ScheduleText.hours(held.weeklyHours)) }
    LaunchedEffect(held.weeklyHours) {
        if (ScheduleText.hoursOf(weeklyHours) != held.weeklyHours) weeklyHours = ScheduleText.hours(held.weeklyHours)
    }
    val today = LocalDate.now()
    val weekdays = DayOfWeek.entries
    val months = held.hoursByMonth.toSortedMap().toList()
    val runs = DayOffRuns.of(held.holidays, held.weeklyDaysOff)
    val upcoming = runs.filter { !it.to.isBefore(today) }
    val earlier = runs.filter { it.to.isBefore(today) }.sumOf { it.days.size }
    SettingsList(listState, top) {
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
        item { InfSectionHeader(text = stringResource(R.string.startmenu_settings_weekly_days_off)) }
        itemsIndexed(weekdays, key = { _, day -> "weekday:${day.name}" }) { index, day ->
            InfGroupedCard(index = index, count = weekdays.size) {
                InfSettingsRow(
                    label = ScheduleText.weekday(day),
                    trailing = {
                        InfSwitch(
                            checked = day in held.weeklyDaysOff,
                            onCheckedChange = { off ->
                                onSchedule { it.copy(weeklyDaysOff = if (off) it.weeklyDaysOff + day else it.weeklyDaysOff - day) }
                            }
                        )
                    }
                )
            }
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
                onClick = onAddMonth
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
                onClick = onAddDays
            )
        }
        if (earlier > 0) {
            item {
                InfSettingsRow(label = pluralStringResource(R.plurals.startmenu_settings_earlier_days, earlier, earlier))
            }
        }
    }
}
