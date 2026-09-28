// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.paraskcd.influentiallauncher.controlcenter.presentation.utils.ControlCenterMetrics
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.molecules.InfSlider
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun SliderRow(
    icon: ImageVector,
    description: String,
    value: Float,
    onChange: (Float) -> Unit,
    onDone: () -> Unit,
    actionIcon: ImageVector,
    actionDescription: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    actionTint: Color = InfTheme.colors.textPrimary
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = InfTheme.colors.textPrimary,
            modifier = Modifier.size(ControlCenterMetrics.sliderIcon)
        )
        InfSlider(
            value = value,
            onValueChange = onChange,
            onValueChangeFinished = onDone,
            contentDescription = description,
            modifier = Modifier.weight(1f)
        )
        InfIconButton(icon = actionIcon, contentDescription = actionDescription, tint = actionTint, onClick = onAction)
    }
}
