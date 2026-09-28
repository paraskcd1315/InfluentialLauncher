// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun InfDatePicker(
    selected: LocalDate,
    onSelect: (LocalDate) -> Unit,
    previousYearDescription: String,
    nextYearDescription: String,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    val colors = InfTheme.colors
    val locale = Locale.getDefault()
    var shown by remember(selected) { mutableStateOf(YearMonth.from(selected)) }
    var pickingYear by remember { mutableStateOf(false) }
    var yearPage by remember { mutableIntStateOf(pageOf(selected.year)) }
    val firstDay = WeekFields.of(locale).firstDayOfWeek

    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s4), modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            InfIconButton(
                icon = Lucide.ChevronLeft,
                contentDescription = previousYearDescription,
                tint = colors.textPrimary,
                onClick = { if (pickingYear) yearPage -= YearsPerPage else shown = shown.minusYears(1) }
            )
            Text(
                text = if (pickingYear) "$yearPage – ${yearPage + YearsPerPage - 1}" else shown.year.toString(),
                fontSize = DsMetrics.pickerYearTextSize,
                fontWeight = FontWeight.Bold,
                color = if (pickingYear) colors.brandText else colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(InfShapes.pill)
                    .clickable {
                        yearPage = pageOf(shown.year)
                        pickingYear = !pickingYear
                    }
                    .padding(vertical = InfSpacing.s2)
            )
            InfIconButton(
                icon = Lucide.ChevronRight,
                contentDescription = nextYearDescription,
                tint = colors.textPrimary,
                onClick = { if (pickingYear) yearPage += YearsPerPage else shown = shown.plusYears(1) }
            )
        }
        if (pickingYear) {
            val years = (yearPage until yearPage + YearsPerPage).toList()
            ChipGrid(
                labels = years.map { it.toString() },
                active = years.indexOf(shown.year),
                onSelect = { index ->
                    shown = shown.withYear(years[index])
                    pickingYear = false
                }
            )
            return@Column
        }
        ChipGrid(
            labels = Month.entries.map { it.getDisplayName(TextStyle.SHORT, locale) },
            active = shown.monthValue - 1,
            onSelect = { index -> shown = shown.withMonth(index + 1) }
        )
        val weekdays = (0 until DaysInWeek).map { firstDay.plus(it.toLong()) }
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdays.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.NARROW, locale),
                    fontSize = DsMetrics.pickerTextSize,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        val leading = (shown.atDay(1).dayOfWeek.value - firstDay.value + DaysInWeek) % DaysInWeek
        val cells = List(leading) { null } + (1..shown.lengthOfMonth()).map { shown.atDay(it) }
        Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s1)) {
            cells.chunked(DaysInWeek).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date -> DayCell(date, date == selected, date == today, onSelect, Modifier.weight(1f)) }
                    repeat(DaysInWeek - week.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun ChipGrid(labels: List<String>, active: Int, onSelect: (Int) -> Unit) {
    val colors = InfTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s2)) {
        labels.withIndex().chunked(ChipColumns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s2), modifier = Modifier.fillMaxWidth()) {
                row.forEach { (index, label) ->
                    val on = index == active
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(DsMetrics.pickerChipHeight)
                            .then(
                                if (on) {
                                    Modifier.clip(InfShapes.pill).background(colors.brand)
                                } else {
                                    Modifier.infGlassSurface(InfShapes.pill, specular = false)
                                }
                            )
                            .clickable { onSelect(index) }
                    ) {
                        Text(
                            text = label,
                            fontSize = DsMetrics.pickerTextSize,
                            fontWeight = FontWeight.SemiBold,
                            color = if (on) colors.onBrand else colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun pageOf(year: Int): Int = year - Math.floorMod(year, YearsPerPage)

@Composable
private fun DayCell(
    date: LocalDate?,
    selected: Boolean,
    today: Boolean,
    onSelect: (LocalDate) -> Unit,
    modifier: Modifier
) {
    val colors = InfTheme.colors
    Box(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1f).padding(InfSpacing.s1)) {
        if (date == null) return@Box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(CircleShape)
                .then(if (selected) Modifier.background(colors.brand) else Modifier)
                .then(if (today && !selected) Modifier.border(DsMetrics.hairlineThickness, colors.brandText, CircleShape) else Modifier)
                .clickable { onSelect(date) }
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = DsMetrics.settingsItemTextSize,
                fontWeight = if (selected || today) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) colors.onBrand else colors.textPrimary
            )
        }
    }
}

private const val ChipColumns = 4
private const val YearsPerPage = 12
private const val DaysInWeek = 7
