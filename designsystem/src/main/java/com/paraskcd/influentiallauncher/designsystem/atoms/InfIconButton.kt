// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer

@Composable
fun InfIconButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = DsMetrics.iconButtonSize,
    glyphSize: Dp = DsMetrics.iconButtonGlyph
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .infParallaxLayer()
            .size(size)
            .alpha(if (enabled) 1f else DsMetrics.disabledAlpha)
            .infGlassSurface(CircleShape, specular = false, strong = true)
            .clickable(enabled = enabled, onClickLabel = contentDescription, onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(glyphSize))
    }
}
