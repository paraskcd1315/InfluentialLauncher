// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Composable
fun AddPageTile(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s3, Alignment.CenterVertically),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(HomeMetrics.iconSize)
                .infGlassSurface(CircleShape, specular = false)
        ) {
            Icon(imageVector = Lucide.Plus, contentDescription = null, tint = LocalWallpaperInk.current.content, modifier = Modifier.size(HomeMetrics.badgeSize))
        }
        Text(text = stringResource(R.string.home_add_page), style = MaterialTheme.typography.labelLarge, color = LocalWallpaperInk.current.content)
    }
}
