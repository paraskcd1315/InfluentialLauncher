// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Sparkles
import com.composables.icons.lucide.SunMedium
import com.composables.icons.lucide.Volume2
import com.composables.icons.lucide.VolumeX
import com.paraskcd.influentiallauncher.controlcenter.R
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ControlState
import com.paraskcd.influentiallauncher.controlcenter.presentation.viewmodels.ControlCenterViewModel
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun ControlCenterPanel(
    state: ControlState,
    viewModel: ControlCenterViewModel,
    onClose: () -> Unit
) {
    Column {
        ToggleGrid(
            state = state,
            onToggle = viewModel::toggle,
            onDetails = {
                viewModel.openDetails(it)
                onClose()
            },
            modifier = Modifier.weight(1f, fill = false)
        )
        Hairline()
        Controls(state, viewModel, onClose)
    }
}

@Composable
private fun Controls(state: ControlState, viewModel: ControlCenterViewModel, onClose: () -> Unit) {
    val colors = InfTheme.colors
    Column {
        Column(
            verticalArrangement = Arrangement.spacedBy(InfSpacing.s4),
            modifier = Modifier.padding(InfSpacing.s5)
        ) {
            SliderRow(
                icon = Lucide.SunMedium,
                description = stringResource(R.string.controlcenter_brightness),
                value = state.brightness,
                onChange = viewModel::setBrightness,
                onDone = viewModel::brightnessDone,
                actionIcon = Lucide.Sparkles,
                actionDescription = stringResource(R.string.controlcenter_auto_brightness),
                onAction = viewModel::toggleAutoBrightness,
                actionTint = if (state.autoBrightness) colors.brandText else colors.textSecondary
            )
            SliderRow(
                icon = if (state.volume <= 0f) Lucide.VolumeX else Lucide.Volume2,
                description = stringResource(R.string.controlcenter_volume),
                value = state.volume,
                onChange = viewModel::setVolume,
                onDone = viewModel::volumeDone,
                actionIcon = Lucide.ChevronRight,
                actionDescription = stringResource(R.string.controlcenter_volume_panel),
                onAction = {
                    viewModel.openVolumePanel()
                    onClose()
                }
            )
        }
        Hairline()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
            modifier = Modifier.padding(horizontal = InfSpacing.s5, vertical = InfSpacing.s3)
        ) {
            val notice = if (state.ready) null else R.string.controlcenter_access_pair
            if (notice != null) {
                Text(
                    text = stringResource(notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.brandText,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = viewModel::requestAccess)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            InfIconButton(
                icon = Lucide.Settings,
                contentDescription = stringResource(R.string.controlcenter_settings),
                tint = colors.textPrimary,
                onClick = {
                    viewModel.openSettings()
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun Hairline() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DsMetrics.hairlineThickness)
            .background(InfTheme.colors.hairline)
    )
}
