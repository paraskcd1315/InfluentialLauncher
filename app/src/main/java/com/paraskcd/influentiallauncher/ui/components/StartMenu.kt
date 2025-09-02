package com.paraskcd.influentiallauncher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paraskcd.influentiallauncher.viewmodels.StartMenuViewModel
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.data.types.AppEntry
import com.paraskcd.influentiallauncher.data.types.SectionEntry
import com.paraskcd.influentiallauncher.ui.modifiers.drawFadingEdges
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StartMenu(modifier: Modifier = Modifier, viewModel: StartMenuViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsState()
    val apps by viewModel.filteredApps.collectAsState()
    val listState: LazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var expandedAppMenu by remember { mutableStateOf<AppEntry?>(null) } // Holds the app for which menu is shown
    var showUninstallConfirmationDialog by remember { mutableStateOf<AppEntry?>(null) }

    fun sectionKey(label: String): Char {
        val c = label.firstOrNull()?.uppercaseChar() ?: '#'
        return if (c in 'A'..'Z') c else '#'
    }

    val sections: List<SectionEntry> by remember(apps) {
        mutableStateOf(
            apps.groupBy { sectionKey(it.label) }
                .toSortedMap(compareBy { ch -> if (ch == '#') '@' else ch }) // '#' primero
                .map { (k, v) -> SectionEntry(k, v.sortedBy { it.label.lowercase() }) }
        )
    }

    fun onClear() {
        viewModel.query.value = ""
    }

    val headerIndexByLetter: Map<Char, Int> = remember(sections) {
        val map = mutableMapOf<Char, Int>()
        var idx = 0
        idx += 1
        sections.forEach { sec ->
            idx += 1
            map[sec.letter] = idx
            idx += 1
            idx += sec.items.size
        }
        map
    }

    val availableLetters: Set<Char> = remember(sections) { sections.map { it.letter }.toSet() }
    var showAlphabet by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
        AnimatedVisibility(
            visible = !showAlphabet,
            enter = fadeIn() + scaleIn(initialScale = 0.98f),
            exit = fadeOut() + scaleOut(targetScale = 0.98f)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawFadingEdges(listState)
                ) {
                    item { Spacer(Modifier.height(72.dp)) }
                    sections.forEach { sec ->
                        item { Spacer(Modifier.height(8.dp)) }
                        stickyHeader(key = "hdr_${sec.letter}") {
                            Surface(
                                shape = RoundedCornerShape(200.dp),
                                color = Color.Transparent,
                                tonalElevation = 0.dp,
                                onClick = { showAlphabet = true }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sec.letter.toString(),
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        itemsIndexed(
                            items = sec.items
                        ) { i, app ->
                            val shape = when {
                                sec.items.size == 1 -> RoundedCornerShape(24.dp)
                                i == 0 -> RoundedCornerShape(
                                    topStart = 24.dp, topEnd = 24.dp,
                                    bottomStart = 8.dp, bottomEnd = 8.dp
                                )

                                i == sec.items.size - 1 -> RoundedCornerShape(
                                    topStart = 8.dp, topEnd = 8.dp,
                                    bottomStart = 24.dp, bottomEnd = 24.dp
                                )

                                else -> RoundedCornerShape(8.dp)
                            }
                            Box {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.65f),
                                    shape = shape,
                                    modifier = Modifier
                                        .padding(vertical = 1.dp)
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            shape = shape
                                        )
                                        .combinedClickable(
                                            onClick = {
                                                onClear()
                                                app.onClick()
                                            },
                                            onLongClick = { expandedAppMenu = app }
                                        )
                                        .semantics {
                                            customActions = listOf(
                                                CustomAccessibilityAction("App info") {
                                                    viewModel.openAppInfo(app.packageName);
                                                    true
                                                },
                                                CustomAccessibilityAction("Pin to Dock") {
                                                    viewModel.pinDock(pkg = app.packageName, activity = app.activityName, label = app.label, rank = 0); true
                                                },
                                                CustomAccessibilityAction("Pin to Home") {
                                                    viewModel.placeHome(pkg = app.packageName, activity = app.activityName, label = app.label, screen = 0, row = 0, column = 0); true
                                                },
                                                CustomAccessibilityAction("Uninstall") {
                                                    showUninstallConfirmationDialog = app;
                                                    true
                                                },
                                            )
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Image(
                                            painter = rememberDrawablePainter(app.icon),
                                            contentDescription = app.label,
                                            modifier = Modifier
                                                .width(54.dp)
                                                .height(54.dp)
                                                .padding(6.dp)
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                                    CircleShape
                                                )
                                                .clip(CircleShape)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = app.label,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = expandedAppMenu == app,
                                    onDismissRequest = { expandedAppMenu = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("App Info") },
                                        onClick = {
                                            viewModel.openAppInfo(pkg = app.packageName)
                                            expandedAppMenu = null
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Pin to Dock") },
                                        onClick = {
                                            viewModel.pinDock(pkg = app.packageName, activity = app.activityName, label = app.label, rank = 0)
                                            expandedAppMenu = null
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Pin to Home") },
                                        onClick = {
                                            viewModel.placeHome(pkg = app.packageName, activity = app.activityName, label = app.label, screen = 0, row = 0, column = 0)
                                            expandedAppMenu = null
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Uninstall") },
                                        onClick = {
                                            showUninstallConfirmationDialog = app
                                            expandedAppMenu = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
                TextField(
                    value = query,
                    onValueChange = { viewModel.query.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(100.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = Color.Gray,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface
                    ),
                    placeholder = { Text("Search...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 24.dp, end = 8.dp)
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = query.isNotEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                onClick = { viewModel.query.value = "" },
                                modifier = Modifier.padding(start = 8.dp, end = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove query",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = showAlphabet,
            enter = fadeIn() + scaleIn(initialScale = 0.96f),
            exit = fadeOut() + scaleOut(targetScale = 0.96f)
        ) {
            AlphabetOverlay(
                availableLetters = availableLetters,
                onLetterClick = { letter ->
                    val target = headerIndexByLetter[letter]
                    if (target != null) {
                        scope.launch {
                            showAlphabet = false
                            delay(120)
                            listState.animateScrollToItem(target)
                        }
                    }
                },
                onDismiss = { showAlphabet = false }
            )
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