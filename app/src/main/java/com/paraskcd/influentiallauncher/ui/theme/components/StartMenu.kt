package com.paraskcd.influentiallauncher.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.paraskcd.influentiallauncher.ui.theme.viewmodels.StartMenuViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.paraskcd.influentiallauncher.data.types.SectionEntry
import com.paraskcd.influentiallauncher.ui.theme.modifiers.drawFadingEdges
import kotlinx.coroutines.launch

@Composable
fun StartMenu(modifier: Modifier = Modifier, viewModel: StartMenuViewModel = hiltViewModel()) {
    val query by viewModel.query.collectAsState()
    val apps by viewModel.filteredApps.collectAsState()
    val listState: LazyListState = rememberLazyListState()

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

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
        Column {
            TextField(
                value = query,
                onValueChange = { viewModel.query.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(100.dp)
                    ),
                shape = RoundedCornerShape(100.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.65f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.65f),
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
                            onClick = {
                                onClear()
                            },
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
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(0.5.dp),
                modifier = Modifier.fillMaxWidth().drawFadingEdges(listState)
            ) {
                sections.forEach { sec ->
                    item { Spacer(Modifier.height(8.dp)) }
                    stickyHeader(key = "hdr_${sec.letter}") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(200.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = sec.letter.toString(),
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
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
                                ),
                            onClick = {
                                onClear()
                                app.onClick()
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
                    }
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}