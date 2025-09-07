package com.paraskcd.influentiallauncher.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.interfaces.GridCell
import com.paraskcd.influentiallauncher.data.types.UiCell
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeGrid(
    rows: Int,
    columns: Int,
    items: List<AppShortcutEntity>,
    currentScreenId: Long?,
    viewModel: LauncherItemsViewModel,
    modifier: Modifier
) {
    val editMode by viewModel.homeEditMode.collectAsState()
    val screenItems = remember(items, currentScreenId) { items.filter { it.screenId == currentScreenId } }
    val occupied = remember(screenItems) { screenItems.associateBy { it.row to it.column } }
    val total = rows * columns

    fun buildCells(): List<UiCell> {
        val sid = currentScreenId ?: -1L
        return List(total) { idx ->
            val r = idx / columns
            val c = idx % columns
            val e = occupied[r to c]
            val content: GridCell = if (e != null) GridCell.App(e) else GridCell.Empty
            val key = e?.id ?: (-1_000_000L - idx)
            UiCell(id = key, cell = content)
        }
    }

    var cells by remember(currentScreenId, screenItems, rows, columns) { mutableStateOf(buildCells()) }

    LaunchedEffect(screenItems, rows, columns, currentScreenId) {
        val newCells = buildCells()
        val oldApps = cells.mapNotNull { (it.cell as? GridCell.App)?.entity?.id }
        val newApps = newCells.mapNotNull { (it.cell as? GridCell.App)?.entity?.id }
        cells = if (oldApps != newApps || cells.size != newCells.size) newCells else cells
    }

    var expandedAppMenu by remember { mutableStateOf<AppShortcutEntity?>(null) }

    val scope = rememberCoroutineScope()
    var persistJob: Job? by remember { mutableStateOf(null) }

    fun persistCurrentOrder() {
        val sid = currentScreenId ?: return
        cells.forEachIndexed { idx, ui ->
            val app = (ui.cell as? GridCell.App)?.entity ?: return@forEachIndexed
            val r = idx / columns
            val c = idx % columns
            viewModel.moveHome(app.id, sid, r, c)
        }
    }

    val lazyGridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        if (!editMode) return@rememberReorderableLazyGridState
        val list = cells.toMutableList()
        val item = list.removeAt(from.index)
        list.add(to.index, item)
        cells = list

        // Debounce persistence so it runs once shortly after you stop moving
        persistJob?.cancel()
        persistJob = scope.launch {
            delay(180)
            persistCurrentOrder()
        }
    }

    DisposableEffect(Unit) {
        onDispose { persistJob?.cancel() }
    }

    LaunchedEffect(editMode) {
        if (!editMode) persistCurrentOrder()
    }

    BoxWithConstraints(modifier = modifier) {
        val gridHeight = maxWidth / columns * rows

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            userScrollEnabled = false,
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyGridState,
            modifier = Modifier.height(gridHeight)
        ) {
            itemsIndexed(
                items = cells,
                key = { _, it -> it.id!! }
            ) { index, ui ->
                ReorderableItem(reorderableState, key = ui.id!!) {
                    val interactionSource = remember { MutableInteractionSource() }
                    Box {
                        when (val cell = ui.cell) {
                            is GridCell.App -> AppCell(
                                entity = cell.entity,
                                editMode = editMode,
                                onOpen = { viewModel.launchApp(cell.entity.packageName) },
                                onAppInfo = { viewModel.openAppInfo(cell.entity.packageName) },
                                onUninstall = { viewModel.uninstallApp(cell.entity.packageName) },
                                onRemoveFromHome = { viewModel.remove(cell.entity.id) },
                                toggleEditMode = { viewModel.setHomeEditMode(!editMode) },
                                showMenu = !editMode,
                                expanded = expandedAppMenu?.id == cell.entity.id,
                                onExpand = { expandedAppMenu = cell.entity },
                                onDismissMenu = { expandedAppMenu = null },
                                icon = viewModel.getAppIcons(cell.entity.packageName)
                            )
                            GridCell.Empty -> EmptyCell(editMode = editMode)
                        }
                        if (editMode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .fillMaxSize()
                                    .draggableHandle(interactionSource = interactionSource)
                                    .clearAndSetSemantics { },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppCell(
    entity: AppShortcutEntity,
    editMode: Boolean,
    onOpen: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onRemoveFromHome: () -> Unit,
    toggleEditMode: () -> Unit,
    showMenu: Boolean,
    expanded: Boolean,
    onExpand: () -> Unit,
    onDismissMenu: () -> Unit,
    icon: Drawable?
) {
    val infinite = rememberInfiniteTransition(label = "home-wiggle")
    val baseAngle by infinite.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "angle"
    )
    val bob by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val rotationZVal = if (editMode) baseAngle else 0f
    val translationYVal = if (editMode) (bob - 0.5f) * 5f else 0f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                rotationZ = rotationZVal
                translationY = translationYVal
            }
            .then(
                if (!editMode && showMenu) {
                    Modifier.combinedClickable(
                        onClick = onOpen,
                        onLongClick = onExpand
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Image(
                painter = rememberDrawablePainter(icon),
                contentDescription = entity.label,
                modifier = Modifier
                    .size(54.dp)
                    .padding(4.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
            )
            Text(
                entity.label,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!editMode && showMenu) {
            DropdownMenu(expanded = expanded, onDismissRequest = onDismissMenu) {
                DropdownMenuItem(text = { Text("Open") }, onClick = { onDismissMenu(); onOpen() })
                DropdownMenuItem(text = { Text("Enable Edit Mode") }, onClick = { onDismissMenu(); toggleEditMode() })
                DropdownMenuItem(text = { Text("App Info") }, onClick = { onDismissMenu(); onAppInfo() })
                DropdownMenuItem(text = { Text("Remove from Home") }, onClick = { onDismissMenu(); onRemoveFromHome() })
                DropdownMenuItem(text = { Text("Uninstall") }, onClick = { onDismissMenu(); onUninstall() })
            }
        }
    }
}

@Composable
private fun EmptyCell(editMode: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                color = Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .then(
                if (editMode) {
                    Modifier.border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        MaterialTheme.shapes.small
                    )
                } else Modifier
            )
    )
}