package com.paraskcd.influentiallauncher.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paraskcd.influentiallauncher.ui.components.HomeGrid
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import com.paraskcd.influentiallauncher.viewmodels.LauncherStateViewModel
import com.paraskcd.influentiallauncher.viewmodels.ScreenManagerViewModel
import kotlinx.coroutines.launch

@Composable
fun ScreenManagerScreen(
    onBack: () -> Unit,
    launcherState: LauncherStateViewModel,
    screenVm: ScreenManagerViewModel = hiltViewModel(),
    launcherVm: LauncherItemsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()

    val screens by launcherState.screens.collectAsState()
    val homeItems by launcherVm.home.collectAsState()
    val activeIndex by launcherState.activeScreenIndex.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = activeIndex.coerceAtLeast(0),
        pageCount = { screens.size + 1 }
    )

    LaunchedEffect(activeIndex, screens.size) {
        val target = activeIndex.coerceIn(0, (screens.size - 1).coerceAtLeast(0))
        if (pagerState.currentPage != target && target >= 0) {
            pagerState.scrollToPage(target)
        }
    }

    BackHandler(enabled = true) { onBack() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background.copy(0.5f)
    ) { inner ->
        Box(modifier = Modifier
            .padding(inner)
            .fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                if (page == screens.size) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surface.copy(0.5f),
                            onClick = {
                                scope.launch {
                                    val newId = screenVm.addScreen()
                                    launcherState.setActiveScreen(newId, persistAsDefault = false)
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    return@HorizontalPager
                }
                val screen = screens[page]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(9f / 16f)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface.copy(0.5f),
                        onClick = {
                            launcherState.setActiveScreenByIndex(page, persistAsDefault = false)
                            onBack()
                        }
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val rows = 5
                            val columns = 4
                            val idealGridHeight = maxWidth / columns * rows
                            val targetWidth =
                                if (idealGridHeight > maxHeight) {
                                    (maxHeight * columns) / rows
                                } else {
                                    maxWidth
                                }

                            HomeGrid(
                                rows = rows,
                                columns = columns,
                                items = homeItems,
                                currentScreenId = screen.id,
                                viewModel = launcherVm,
                                modifier = Modifier.width(targetWidth)
                            )
                        }
                        AssistChip(
                            onClick = { screenVm.setDefault(screen.id) },
                            label = { Text(if (screen.isDefault) "Home" else "Mark as home") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (screen.isDefault) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopStart)
                        )
                    }
                }
            }
        }
    }
}