package com.paraskcd.influentiallauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.interfaces.GridCell
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import kotlinx.coroutines.launch
import kotlin.math.floor

@Composable
fun HomeGrid(
    rows: Int,
    columns: Int,
    items: List<AppShortcutEntity>,
    currentScreen: Int,
    viewModel: LauncherItemsViewModel,
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()
    val screenItems = remember(items, currentScreen) { items.filter { it.screen == currentScreen } }
    val occupied = remember(screenItems) { screenItems.associateBy { Pair(it.row, it.column) } }
    val cells: List<GridCell> = remember(screenItems, rows, columns) {
        buildList {
            for (r in 0 until rows) {
                for (c in 0 until columns) {
                    val e = occupied[Pair(r, c)]
                    add(if (e != null) GridCell.App(e) else GridCell.Empty)
                }
            }
        }
    }
    var dragging by remember { mutableStateOf<AppShortcutEntity?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Layout(
        modifier = modifier.pointerInput(screenItems) {
            detectDragGestures(
                onDragStart = { offset ->
                    val cellSizePx = size.width / columns.toFloat()
                    val col = floor(offset.x / cellSizePx).toInt()
                    val row = floor(offset.y / cellSizePx).toInt()
                    dragging = screenItems.firstOrNull { it.row == row && it.column == col }
                },
                onDrag = { change, amount ->
                    change.consume()
                    dragOffset += amount
                },
                onDragEnd = {
                    val entity = dragging
                    if (entity != null) {
                        val cellSizePx = size.width / columns.toFloat()
                        val originX = (entity.column ?: 0) * cellSizePx
                        val originY = (entity.row ?: 0) * cellSizePx
                        val finalX = originX + dragOffset.x
                        val finalY = originY + dragOffset.y
                        val targetCol = (finalX / cellSizePx).toInt().coerceIn(0, columns - 1)
                        val targetRow = (finalY / cellSizePx).toInt().coerceIn(0, rows - 1)
                        val occupiedTarget = screenItems.any { it.row == targetRow && it.column == targetCol && it.id != entity.id }
                        if (!occupiedTarget && (targetRow != entity.row || targetCol != entity.column)) {
                            scope.launch {
                                viewModel.moveHome(entity.id, currentScreen, targetRow, targetCol)
                            }
                        }
                    }
                    dragging = null
                    dragOffset = Offset.Zero
                },
                onDragCancel = {
                    dragging = null
                    dragOffset = Offset.Zero
                }
            )
        },
        content = {
            cells.forEach { cell ->
                when (cell) {
                    is GridCell.App -> AppCell(cell.entity, isDragging = dragging?.id == cell.entity.id)
                    GridCell.Empty -> EmptyCell()
                }
            }
        }
    )  { measurables, constraints ->
        val cellSize = constraints.maxWidth / columns
        val cellConstraints = constraints.copy(
            minWidth = cellSize,
            maxWidth = cellSize,
            minHeight = cellSize,
            maxHeight = cellSize
        )
        val placeables = measurables.map { it.measure(cellConstraints) }
        val height = cellSize * rows
        layout(constraints.maxWidth, height) {
            var i = 0
            for (r in 0 until rows) {
                for (c in 0 until columns) {
                    val p = placeables[i++]
                    p.place(c * cellSize, r * cellSize)
                }
            }
        }
    }
}

@Composable
private fun AppCell(entity: AppShortcutEntity, isDragging: Boolean) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                if (isDragging) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                MaterialTheme.shapes.medium
            )
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), MaterialTheme.shapes.medium)
    )
}

@Composable
private fun EmptyCell() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.05f),
                MaterialTheme.shapes.medium
            )
    )
}