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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.min
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.presentation.state.GridSlot
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics
import kotlin.math.roundToInt

@Composable
fun PageGrid(
    slots: List<GridSlot>,
    grid: HomeGrid,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    wiggle: Float,
    onRemove: ((LauncherApp) -> Unit)?,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val cellWidth = maxWidth / grid.columns
        val cellHeight = min(maxHeight / grid.rows, HomeMetrics.maxCellHeight)
        val cellWidthPx = with(density) { cellWidth.toPx() }
        val cellHeightPx = with(density) { cellHeight.toPx() }
        val topPx = with(density) { ((maxHeight - cellHeight * grid.rows) / 2).toPx() }
        val iconSize = cellIconSize(cellWidth, cellHeight)
        slots.forEachIndexed { index, slot ->
            val target = IntOffset(
                ((index % grid.columns) * cellWidthPx).roundToInt(),
                (topPx + (index / grid.columns) * cellHeightPx).roundToInt()
            )
            when (slot) {
                GridSlot.Empty -> Unit
                GridSlot.Target -> Box(
                    modifier = Modifier
                        .offset { target }
                        .size(cellWidth, cellHeight)
                        .padding(InfSpacing.s2)
                        .border(DsMetrics.hairlineThickness, InfTheme.colors.border, InfShapes.md)
                )
                is GridSlot.App -> key(slot.app.id.key) {
                    val position by animateIntOffsetAsState(target, label = "homeSlot")
                    val turn = if (index % 2 == 0) wiggle else -wiggle
                    HomeAppCell(
                        app = slot.app,
                        loadIcon = loadIcon,
                        iconSize = iconSize,
                        onRemove = onRemove?.let { remove -> { remove(slot.app) } },
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

@Composable
private fun cellIconSize(cellWidth: Dp, cellHeight: Dp): Dp {
    val style = MaterialTheme.typography.labelMedium
    val labelHeight = with(LocalDensity.current) {
        if (style.lineHeight.isSp) style.lineHeight.toDp() else style.fontSize.toDp() * HomeMetrics.labelLineFallback
    }
    val byHeight = cellHeight - HomeMetrics.labelGap - labelHeight - HomeMetrics.cellInset
    val byWidth = cellWidth - HomeMetrics.cellInset
    return min(HomeMetrics.iconSize, min(byHeight, byWidth)).coerceAtLeast(HomeMetrics.badgeSize)
}
