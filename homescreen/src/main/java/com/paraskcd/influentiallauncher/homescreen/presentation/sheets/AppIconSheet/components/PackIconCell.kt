// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.sheets.AppIconSheet.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.AppIconMetrics

@Composable
fun PackIconCell(
    key: String,
    label: String,
    load: suspend (Int) -> Bitmap?,
    onPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(InfShapes.md)
            .clickable(onClickLabel = label, onClick = onPick)
            .padding(InfSpacing.s2)
    ) {
        InfAsyncIcon(key = key, size = AppIconMetrics.cellIcon, load = load)
    }
}
