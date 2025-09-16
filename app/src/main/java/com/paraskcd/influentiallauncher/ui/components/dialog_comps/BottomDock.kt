package com.paraskcd.influentiallauncher.ui.components.dialog_comps

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.data.db.entities.AppShortcutEntity
import com.paraskcd.influentiallauncher.data.types.AppMenuAction
import com.paraskcd.influentiallauncher.ui.dialogs.AppContextMenuDialog
import com.paraskcd.influentiallauncher.ui.dialogs.DockDialog
import com.paraskcd.influentiallauncher.ui.dialogs.StartMenuDialog
import com.paraskcd.influentiallauncher.ui.icons.WindowsIcon
import com.paraskcd.influentiallauncher.ui.icons.WindowsOpenedIcon
import com.paraskcd.influentiallauncher.ui.modifiers.drawHorizontalFadingEdges
import com.paraskcd.influentiallauncher.utils.ReorderableItemCustom
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.util.Random
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomDock(modifier: Modifier = Modifier, context: Context, viewModel: LauncherItemsViewModel = hiltViewModel()) {
    val isOpen by StartMenuDialog.isOpenFlow.collectAsState()
    val dockFlowState = viewModel.dock.collectAsState()
    val editMode by viewModel.dockEditMode.collectAsState()

    // Local working order (so DB updates don’t interrupt active drag)
    var dockOrder by remember { mutableStateOf(listOf<AppShortcutEntity>()) }

    // Sync local order when underlying data changes and we are not mid manual adjustment
    LaunchedEffect(dockFlowState.value) {
        // Only replace if different ids order (avoid wiping local reorder mid drag)
        val newList = dockFlowState.value
        if (dockOrder.map { it.id } != newList.map { it.id }) {
            dockOrder = newList
        } else if (dockOrder.size != newList.size) {
            dockOrder = newList
        }
    }

    var expandedAppMenu by remember { mutableStateOf<AppShortcutEntity?>(null) }
    var showUninstallConfirmationDialog by remember { mutableStateOf<AppShortcutEntity?>(null) }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            if (!editMode) return@rememberReorderableLazyListState
            val mutable = dockOrder.toMutableList()
            val item = mutable.removeAt(from.index)
            mutable.add(to.index, item)
            dockOrder = mutable
        },
        onDragEnd = { _, _ ->
            if (editMode) {
                viewModel.updateDockOrder(dockOrder)
            }
        }
    )

    val dragRowModifier: Modifier = if (editMode) {
        Modifier
            .reorderable(reorderState)
            .detectReorderAfterLongPress(reorderState)
    } else {
        Modifier
    }

    val multipleClicksModifier: (app: AppShortcutEntity) -> Modifier = { app ->
        if (!editMode) {
            Modifier.combinedClickable(
                onClick = {
                    viewModel.launchApp(app.packageName)
                },
                onLongClick = {
                    expandedAppMenu = app
                }
            )
        } else {
            Modifier.semantics {
                customActions = listOf(
                    CustomAccessibilityAction("Disable Edit Mode") {
                        viewModel.setDockEditMode(false); true
                    }
                )
            }
        }
    }

    val iconTintColorCompose = MaterialTheme.colorScheme.primary
    val iconTintColorArgb = remember(iconTintColorCompose) { iconTintColorCompose.toArgb() }

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    if (isOpen) {
                        StartMenuDialog.close()
                    } else {
                        StartMenuDialog.showOrUpdate(context)
                    }
                },
                modifier = Modifier
                    .padding(top = 24.dp, start = 8.dp, bottom = 24.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isOpen)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        else
                            Color.Transparent
                    )
                    .border(
                        width = 1.dp,
                        color = if (isOpen)
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    imageVector =
                        if (isOpen) {
                            WindowsOpenedIcon
                        } else {
                            WindowsIcon
                        },
                    contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            LazyRow(
                state = reorderState.listState,
                modifier = dragRowModifier
                    .drawHorizontalFadingEdges(
                        scrollableState = reorderState.listState,
                        leftEdgeWidth = 32.dp,
                        rightEdgeWidth = 32.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(dockOrder, key = { it.id }) { app ->
                    ReorderableItemCustom(
                        reorderState,
                        key = app.id
                    ) { dragging ->
                        val wiggleSeed = remember(app.id) { Random(app.id) }
                        val phase = remember(app.id) { wiggleSeed.nextFloat() }
                        val infinite = rememberInfiniteTransition(label = "dock-wiggle")

                        val baseAngle by infinite.animateFloat(
                            initialValue = -3f,
                            targetValue = 3f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(300, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "angle"
                        )
                        val bobAngle by infinite.animateFloat(
                            initialValue = 0f,
                            targetValue = (Math.PI * 2).toFloat(),
                            animationSpec = infiniteRepeatable(
                                animation = tween(900, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "bob"
                        )
                        val rotationZVal = if (editMode) baseAngle * (0.7f + phase * 0.6f) else 0f
                        val verticalOffset = if (editMode) (sin(bobAngle + phase * 6f) * 2.5f).toFloat() else 0f
                        val draggingBg = if (dragging) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color.Transparent
                        var iconBounds by remember { mutableStateOf<Rect?>(null) }

                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    // FIX: Must assign rotationZ
                                    rotationZ = rotationZVal
                                    translationY = verticalOffset
                                }
                                .background(draggingBg, RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = multipleClicksModifier(app)
                                    .onGloballyPositioned { lc ->
                                        val b = lc.boundsInWindow()
                                        val dockLoc = IntArray(2)
                                        DockDialog
                                            .getDecorView()
                                            ?.getLocationOnScreen(dockLoc)
                                        val winX = dockLoc.getOrNull(0) ?: 0
                                        val winY = dockLoc.getOrNull(1) ?: 0
                                        iconBounds = Rect(
                                            (b.left + winX).toInt(),
                                            (b.top + winY).toInt(),
                                            (b.right + winX).toInt(),
                                            (b.bottom + winY).toInt()
                                        )
                                    }
                                    .clip(RoundedCornerShape(14.dp))
                                    .semantics {
                                        customActions = listOf(
                                            CustomAccessibilityAction(
                                                if (editMode) "Disable Edit Mode" else "Enable Edit Mode"
                                            ) {
                                                viewModel.setDockEditMode(!editMode); true
                                            },
                                            CustomAccessibilityAction("App info") {
                                                viewModel.openAppInfo(app.packageName); true
                                            },
                                            CustomAccessibilityAction("Unpin from Dock") {
                                                viewModel.remove(id = app.id); true
                                            }
                                        )
                                    }
                            ) {
                                Image(
                                    painter = rememberDrawablePainter(viewModel.getAppIcons(app.packageName, iconTintColorArgb)),
                                    contentDescription = app.label,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            CircleShape
                                        )
                                        .clip(CircleShape)
                                )
                            }

                            // Per-item menu button only in edit mode (since long press reserved for drag)
                            if (editMode) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(22.dp) // final diameter
                                        .clip(CircleShape)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                            CircleShape
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            CircleShape
                                        )
                                        .semantics { } // (optional: add role if needed)
                                        .clickable { expandedAppMenu = app },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "⋮",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                }
                            }

                            LaunchedEffect(expandedAppMenu) {
                                if (expandedAppMenu == app && iconBounds != null) {
                                    AppContextMenuDialog.show(
                                        context = context,
                                        anchorRect = iconBounds!!,
                                        appLabel = app.label,
                                        appIcon = viewModel.getAppIcons(app.packageName, iconTintColorArgb),
                                        onOpenApp = { viewModel.launchApp(app.packageName) },
                                        actions = buildList {
                                            if (!editMode) add(AppMenuAction("Enable Edit Mode") {
                                                viewModel.setDockEditMode(
                                                    true
                                                )
                                            })
                                            else add(AppMenuAction("Disable Edit Mode") { viewModel.setDockEditMode(false) })
                                            add(AppMenuAction("App Info") { viewModel.openAppInfo(app.packageName) })
                                            add(AppMenuAction("Unpin from Dock") { viewModel.remove(app.id) })
                                            add(AppMenuAction("Uninstall", destructive = true) {
                                                showUninstallConfirmationDialog = app
                                            })
                                        }
                                    )
                                    expandedAppMenu = null
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    showUninstallConfirmationDialog?.let { app ->
        BasicAlertDialog(
            onDismissRequest = { showUninstallConfirmationDialog = null}
        ) {
            Surface(shape = RoundedCornerShape(16.dp)) {
                Column {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Uninstall ${app.label}?", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Are you sure you want to uninstall ${app.label}? This action cannot be undone.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceBright),
                    ) {
                        TextButton(onClick = { showUninstallConfirmationDialog = null }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            viewModel.uninstallApp(pkg = app.packageName)
                            showUninstallConfirmationDialog = null
                        }) {
                            Text("Uninstall", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

            }
        }
    }
}