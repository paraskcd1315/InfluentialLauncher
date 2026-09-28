// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.clock.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paraskcd.influentiallauncher.clock.presentation.utils.ClockFormat
import com.paraskcd.influentiallauncher.clock.presentation.utils.ClockMetrics
import com.paraskcd.influentiallauncher.clock.presentation.viewmodels.ClockViewModel
import com.paraskcd.influentiallauncher.designsystem.theme.LocalWallpaperInk

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    sideInset: Dp = ClockMetrics.sideInset,
    viewModel: ClockViewModel = hiltViewModel()
) {
    val now by viewModel.now.collectAsStateWithLifecycle()
    val locale = LocalConfiguration.current.locales[0]
    val ink = LocalWallpaperInk.current
    val shadow = Shadow(
        color = ink.shadow.copy(alpha = ClockMetrics.shadowAlpha),
        offset = ClockMetrics.shadowOffset,
        blurRadius = ClockMetrics.shadowBlur
    )
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBarsIgnoringVisibility)
            .padding(start = sideInset, end = sideInset, top = ClockMetrics.topGap)
    ) {
        Text(
            text = ClockFormat.time(now, locale),
            style = MaterialTheme.typography.displayLarge.copy(fontSize = ClockMetrics.timeSize, shadow = shadow),
            color = ink.content
        )
        Spacer(Modifier.height(ClockMetrics.dateGap))
        Text(
            text = ClockFormat.date(now, locale),
            style = MaterialTheme.typography.titleMedium.copy(shadow = shadow),
            color = ink.content.copy(alpha = ClockMetrics.dateAlpha)
        )
    }
}
