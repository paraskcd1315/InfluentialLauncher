// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Trash2
import com.paraskcd.influentiallauncher.designsystem.atoms.InfIconButton
import com.paraskcd.influentiallauncher.designsystem.foundation.horizontalFadingEdges
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.homescreen.R
import com.paraskcd.influentiallauncher.homescreen.presentation.state.VisualPage
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeGrid
import com.paraskcd.influentiallauncher.homescreen.presentation.utils.HomeMetrics

@Composable
fun PageOverview(
    pages: List<VisualPage>,
    homeIndex: Int,
    currentIndex: Int,
    onOpen: (Int) -> Unit,
    onSetHome: (VisualPage) -> Unit,
    onDelete: (VisualPage) -> Unit,
    onAdd: () -> Unit,
    grid: HomeGrid,
    loadIcon: suspend (AppId, Int) -> Bitmap?,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val cardWidth = maxWidth * HomeMetrics.overviewCardFraction
        val cardHeight = maxHeight * HomeMetrics.overviewCardFraction
        val side = (maxWidth - cardWidth) / 2
        val listState = rememberLazyListState()
        LaunchedEffect(Unit) { listState.scrollToItem(currentIndex.coerceIn(0, pages.size)) }
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(HomeMetrics.overviewGap),
            contentPadding = PaddingValues(horizontal = side),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .horizontalFadingEdges(listState)
        ) {
            itemsIndexed(pages, key = { _, page -> page.key }) { index, page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)
                ) {
                    Box(
                        modifier = Modifier
                            .width(cardWidth)
                            .height(cardHeight)
                            .infGlassSurface(RoundedCornerShape(HomeMetrics.pageCornerRadius))
                            .clickable(onClickLabel = stringResource(R.string.home_open_page, index + 1)) { onOpen(index) }
                            .padding(InfSpacing.s2)
                    ) {
                        PageMap(apps = page.slots, grid = grid, loadIcon = loadIcon)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
                        val home = index == homeIndex
                        InfIconButton(
                            icon = Lucide.House,
                            contentDescription = stringResource(if (home) R.string.home_is_home else R.string.home_make_home),
                            tint = if (home) colors.brandText else colors.textSecondary,
                            onClick = { onSetHome(page) }
                        )
                        InfIconButton(
                            icon = Lucide.Trash2,
                            contentDescription = stringResource(R.string.home_delete_page),
                            tint = colors.dangerText,
                            onClick = { onDelete(page) },
                            enabled = pages.size > 1
                        )
                    }
                }
            }
            item(key = AddKey) {
                Column(verticalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
                    Box(
                        modifier = Modifier
                            .width(cardWidth)
                            .height(cardHeight)
                            .infGlassSurface(RoundedCornerShape(HomeMetrics.pageCornerRadius), specular = false)
                            .clickable(onClickLabel = stringResource(R.string.home_add_page), onClick = onAdd)
                    ) {
                        AddPageTile()
                    }
                    Spacer(modifier = Modifier.height(DsMetrics.iconButtonSize))
                }
            }
        }
    }
}

private const val AddKey = "add"
