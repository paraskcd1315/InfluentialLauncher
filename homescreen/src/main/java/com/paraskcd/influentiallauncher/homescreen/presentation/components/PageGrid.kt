package com.paraskcd.influentiallauncher.homescreen.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.domain.model.HomeLayout
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics
import kotlin.math.roundToInt

@Composable
fun PageGrid(
    slots: List<LauncherApp?>,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    wiggle: Float,
    onRemove: ((LauncherApp) -> Unit)?,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val cellWidth = maxWidth / HomeMetrics.columns
        val cellHeight = maxHeight / HomeMetrics.rows
        val cellWidthPx = with(density) { cellWidth.toPx() }
        val cellHeightPx = with(density) { cellHeight.toPx() }
        slots.take(HomeLayout.PageCapacity).forEachIndexed { index, app ->
            val target = IntOffset(
                ((index % HomeMetrics.columns) * cellWidthPx).roundToInt(),
                ((index / HomeMetrics.columns) * cellHeightPx).roundToInt()
            )
            if (app == null) {
                Box(
                    modifier = Modifier
                        .offset { target }
                        .size(cellWidth, cellHeight)
                        .padding(InfSpacing.s2)
                        .border(DsMetrics.hairlineThickness, InfTheme.colors.border, InfShapes.md)
                )
            } else {
                key(app.id.key) {
                    val position by animateIntOffsetAsState(target, label = "homeSlot")
                    val turn = if (index % 2 == 0) wiggle else -wiggle
                    HomeAppCell(
                        app = app,
                        loadIcon = loadIcon,
                        onRemove = onRemove?.let { remove -> { remove(app) } },
                        modifier = Modifier
                            .offset { position }
                            .size(cellWidth, cellHeight)
                            .graphicsLayer { rotationZ = turn }
                    )
                }
            }
        }
    }
}
