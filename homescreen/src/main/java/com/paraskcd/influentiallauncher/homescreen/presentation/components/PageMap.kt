// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.min
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Composable
fun PageMap(apps: List<LauncherApp?>, grid: HomeGrid, loadIcon: suspend (AppId, Int) -> Bitmap?, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val cellWidth = maxWidth / grid.columns
        val cellHeight = maxHeight / grid.rows
        val icon = min(cellWidth, cellHeight) * HomeMetrics.mapIconFraction
        apps.take(grid.capacity).forEachIndexed { index, app ->
            if (app != null) key(app.id.key) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(x = cellWidth * (index % grid.columns), y = cellHeight * (index / grid.columns))
                        .size(cellWidth, cellHeight)
                ) {
                    InfAsyncIcon(key = app.id.key, size = icon, load = { loadIcon(app.id, it) }, version = loadIcon)
                }
            }
        }
    }
}
