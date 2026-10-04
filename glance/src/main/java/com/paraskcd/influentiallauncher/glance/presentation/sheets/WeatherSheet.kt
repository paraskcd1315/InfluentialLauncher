// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.composables.icons.lucide.BookmarkCheck
import com.composables.icons.lucide.BookmarkPlus
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfSectionHeader
import com.paraskcd.influentiallauncher.glance.presentation.utils.PlaceLabels
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSegmented
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.model.WeatherSheetState
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.DailyList.DailyList
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.HourlyRow
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.WarningList
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.WeatherExtras
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.WeatherNow
import com.paraskcd.influentiallauncher.glance.presentation.sheets.components.WeatherSheetSkeleton
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherSourceName
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun WeatherSheet(
    state: WeatherSheetState?,
    onSelect: (WeatherSourceName) -> Unit,
    savedKeys: Set<String>,
    onPlaces: () -> Unit,
    onToggleSaved: (Place) -> Unit,
    onDismiss: () -> Unit
) {
    InfSheetWindow(
        item = state,
        title = { current -> current.report?.place?.let(PlaceLabels::nameOf) ?: stringResource(R.string.weather_title) },
        onDismiss = onDismiss,
        edgeToEdge = true,
        onTitleClick = { onPlaces() },
        trailing = { current ->
            val place = current.report?.place
            if (place != null) {
                val saved = place.key() in savedKeys
                InfIconButton(
                    icon = if (saved) Lucide.BookmarkCheck else Lucide.BookmarkPlus,
                    contentDescription = stringResource(if (saved) R.string.weather_unsave_location else R.string.weather_save_location),
                    tint = if (saved) InfTheme.colors.brandText else InfTheme.colors.textPrimary,
                    onClick = { onToggleSaved(place) }
                )
            }
        },
        header = { current ->
            val sources = current.report?.sources.orEmpty()
            if (sources.size > 1) {
                InfSegmented(
                    labels = sources.map { stringResource(WeatherVisuals.sourceOf(it)) },
                    selected = sources.indexOf(current.selected).coerceAtLeast(0),
                    onSelect = { onSelect(sources[it]) },
                    blurred = LocalWindowBlurred.current,
                    modifier = Modifier.fillMaxWidth(),
                    equalWidth = true
                )
            }
        }
    ) { current ->
        val report = current.report
        Column(
            verticalArrangement = Arrangement.spacedBy(InfSpacing.s5),
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = InfSpacing.s5)
        ) {
            if (current.loading) {
                WeatherSheetSkeleton()
                return@Column
            }
            if (report == null) {
                SheetNote(stringResource(R.string.weather_unavailable))
                return@Column
            }
            val forecast = report.forecast
            Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s5)) {
                WeatherNow(forecast = forecast, today = forecast.days.firstOrNull(), modifier = Modifier.padding(horizontal = InfSpacing.s5))
                if (forecast.hours.isNotEmpty()) {
                    Section(stringResource(R.string.weather_next_hours)) {
                        HourlyRow(hours = forecast.hours, horizontalInset = InfSpacing.s5)
                    }
                }
                if (forecast.days.isNotEmpty()) {
                    Section(stringResource(R.string.weather_next_days)) {
                        DailyList(days = forecast.days, modifier = Modifier.padding(horizontal = InfSpacing.s5))
                    }
                }
                Section(stringResource(R.string.weather_extras)) {
                    WeatherExtras(forecast = forecast, airQuality = report.airQuality, modifier = Modifier.padding(horizontal = InfSpacing.s5))
                }
                if (report.warnings.isNotEmpty()) {
                    Section(stringResource(R.string.weather_warnings)) {
                        WarningList(warnings = report.warnings, modifier = Modifier.padding(horizontal = InfSpacing.s5))
                    }
                }
                SheetNote(
                    stringResource(
                        R.string.weather_attribution,
                        stringResource(WeatherVisuals.creditOf(forecast.source))
                    )
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        InfSectionHeader(text = title, modifier = Modifier.padding(horizontal = InfSpacing.s1))
        content()
    }
}

@Composable
private fun SheetNote(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = InfTheme.colors.textTertiary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = InfSpacing.s5)
    )
}
