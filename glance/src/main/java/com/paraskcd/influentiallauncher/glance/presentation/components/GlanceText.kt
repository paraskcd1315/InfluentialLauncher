// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.glance.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.glance.presentation.utils.GlanceMetrics

@Composable
fun GlanceText(text: String, style: TextStyle, modifier: Modifier = Modifier, alpha: Float = 1f) {
    val ink = LocalWallpaperInk.current
    Text(
        text = text,
        style = style.copy(
            shadow = Shadow(
                color = ink.shadow.copy(alpha = GlanceMetrics.shadowAlpha),
                offset = GlanceMetrics.shadowOffset,
                blurRadius = GlanceMetrics.shadowBlur
            )
        ),
        color = ink.content.copy(alpha = alpha),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
