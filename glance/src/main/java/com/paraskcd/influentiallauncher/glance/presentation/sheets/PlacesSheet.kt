// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Navigation
import com.composables.icons.lucide.X
import com.composables.icons.lucide.Plus
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.atoms.InfTextField
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.model.PlacePicker
import com.paraskcd.influentiallauncher.glance.presentation.utils.PlaceLabels
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.weather.domain.model.Place
import com.paraskcd.influentiallauncher.windowing.presentation.InfSheetWindow

@Composable
fun PlacesSheet(
    picker: PlacePicker?,
    places: List<Place>,
    selected: Place?,
    onSearch: (String) -> Unit,
    onPick: (Place?) -> Unit,
    onAdd: (Place) -> Unit,
    onRemove: (Place) -> Unit,
    onToggleAdding: () -> Unit,
    onDismiss: () -> Unit
) {
    InfSheetWindow(
        item = picker,
        title = { stringResource(if (it.adding) R.string.weather_add_location else R.string.weather_locations) },
        onDismiss = onDismiss,
        trailing = { current ->
            InfIconButton(
                icon = if (current.adding) Lucide.X else Lucide.Plus,
                contentDescription = stringResource(if (current.adding) R.string.weather_remove_cancel else R.string.weather_add_location),
                tint = InfTheme.colors.textPrimary,
                onClick = onToggleAdding
            )
        }
    ) { current ->
        val maxHeight = (LocalConfiguration.current.screenHeightDp * WeatherSheetMetrics.placesMaxFraction).dp
        if (current.adding) {
            InfTextField(
                value = current.query,
                onValueChange = onSearch,
                label = stringResource(R.string.weather_add_location),
                placeholder = stringResource(R.string.weather_search_location),
                modifier = Modifier.padding(bottom = InfSpacing.s4)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap),
            modifier = Modifier
                .heightIn(max = maxHeight)
                .verticalScroll(rememberScrollState())
        ) {
            if (current.adding) {
                if (current.query.isBlank()) return@Column
                when {
                    current.searching -> Note(stringResource(R.string.weather_searching))
                    current.results.isEmpty() -> Note(stringResource(R.string.weather_no_results))
                    else -> current.results.forEachIndexed { index, place ->
                        PlaceRow(
                            icon = Lucide.MapPin,
                            title = PlaceLabels.nameOf(place),
                            detail = PlaceLabels.detailOf(place),
                            index = index,
                            count = current.results.size,
                            onClick = { onAdd(place) }
                        )
                    }
                }
            } else {
                val count = places.size + 1
                PlaceRow(
                    icon = Lucide.Navigation,
                    title = stringResource(R.string.weather_current_location),
                    detail = null,
                    index = 0,
                    count = count,
                    checked = selected == null,
                    onClick = { onPick(null) }
                )
                places.forEachIndexed { index, place ->
                    PlaceRow(
                        icon = Lucide.MapPin,
                        title = PlaceLabels.nameOf(place),
                        detail = PlaceLabels.detailOf(place),
                        index = index + 1,
                        count = count,
                        checked = selected?.key() == place.key(),
                        onClick = { onPick(place) },
                        onRemove = { onRemove(place) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceRow(
    icon: ImageVector,
    title: String,
    detail: String?,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    checked: Boolean = false,
    onRemove: (() -> Unit)? = null
) {
    val colors = InfTheme.colors
    InfGroupedCard(index = index, count = count) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = title, onClick = onClick)
                .padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s3)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(DsMetrics.actionIconSize))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                detail?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = colors.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (checked) {
                Icon(imageVector = Lucide.Check, contentDescription = null, tint = colors.brandText, modifier = Modifier.size(DsMetrics.actionIconSize))
            }
            if (onRemove != null) {
                val label = stringResource(R.string.weather_remove_location, title)
                Icon(
                    imageVector = Lucide.X,
                    contentDescription = label,
                    tint = colors.textSecondary,
                    modifier = Modifier
                        .size(DsMetrics.iconButtonSize)
                        .clickable(onClickLabel = label, onClick = onRemove)
                        .padding(InfSpacing.s3)
                )
            }
        }
    }
}

@Composable
private fun Note(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = InfTheme.colors.textSecondary,
        modifier = Modifier.padding(InfSpacing.s2)
    )
}
