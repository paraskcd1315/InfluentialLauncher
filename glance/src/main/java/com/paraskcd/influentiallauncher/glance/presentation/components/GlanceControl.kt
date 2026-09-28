// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics

@Composable
fun GlanceControl(icon: ImageVector, description: String, onClick: () -> Unit, tint: Color = InfTheme.colors.textPrimary) {
    InfIconButton(
        icon = icon,
        contentDescription = description,
        tint = tint,
        onClick = onClick,
        size = GlanceMetrics.control,
        glyphSize = GlanceMetrics.controlGlyph
    )
}
