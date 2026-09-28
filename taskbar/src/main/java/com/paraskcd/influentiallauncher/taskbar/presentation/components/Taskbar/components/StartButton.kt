// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.taskbar.presentation.components.Taskbar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.designsystem.foundation.infAccentGradientTint
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.icons.WindowsLogo
import com.paraskcd.influentiallauncher.taskbar.R
import com.paraskcd.influentiallauncher.taskbar.presentation.utils.TaskbarMetrics

@Composable
fun StartButton(
    open: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(TaskbarMetrics.startCornerRadius)
    val label = stringResource(R.string.taskbar_start)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(TaskbarMetrics.startSize)
            .then(if (open) Modifier.infGlassSurface(shape, specular = false, strong = true) else Modifier.clip(shape))
            .clickable(onClickLabel = label, onClick = onClick)
    ) {
        Icon(
            imageVector = WindowsLogo,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier
                .size(TaskbarMetrics.startGlyphSize)
                .infAccentGradientTint()
        )
    }
}
