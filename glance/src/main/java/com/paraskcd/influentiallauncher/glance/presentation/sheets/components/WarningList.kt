package com.paraskcd.influentiallauncher.glance.presentation.sheets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.R
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherSheetMetrics
import com.paraskcd.influentiallauncher.glance.presentation.utils.WeatherVisuals
import com.paraskcd.influentiallauncher.weather.domain.model.WeatherWarning
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val WarningTimeFormat = DateTimeFormatter.ofPattern("EEE HH:mm")

@Composable
fun WarningList(warnings: List<WeatherWarning>, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val zone = ZoneId.systemDefault()
    Column(verticalArrangement = Arrangement.spacedBy(DsMetrics.groupGap), modifier = modifier.fillMaxWidth()) {
        warnings.forEachIndexed { index, warning ->
            InfGroupedCard(index = index, count = warnings.size) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(horizontal = InfSpacing.s4, vertical = InfSpacing.s3)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = InfSpacing.s1)
                            .size(WeatherSheetMetrics.warningDot)
                            .clip(CircleShape)
                            .background(Color(WeatherVisuals.warningArgbOf(warning.level)))
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s1), modifier = Modifier.weight(1f)) {
                        Text(text = warning.headline, style = MaterialTheme.typography.titleSmall, color = colors.textPrimary)
                        val onset = warning.onset?.atZoneSameInstant(zone)?.format(WarningTimeFormat)
                        val expires = warning.expires?.atZoneSameInstant(zone)?.format(WarningTimeFormat)
                        val areas = warning.areas.joinToString(", ")
                        Text(
                            text = if (onset != null && expires != null) {
                                stringResource(R.string.weather_warning_when, areas, onset, expires)
                            } else {
                                areas
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                        warning.description?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
