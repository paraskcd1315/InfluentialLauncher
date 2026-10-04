// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfBlurred
import com.paraskcd.influentiallauncher.designsystem.foundation.SheetDrag
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.infHazeBar
import com.paraskcd.influentiallauncher.designsystem.foundation.infHazeSource
import com.paraskcd.influentiallauncher.designsystem.foundation.rememberInfHazeArea
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfBottomSheet(
    drag: SheetDrag,
    maxHeight: Dp,
    bottomInset: Dp,
    title: String,
    leading: (@Composable () -> Unit)? = null,
    edgeToEdge: Boolean = false,
    header: (@Composable ColumnScope.() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = InfTheme.colors
    val shape = RoundedCornerShape(topStart = InfRadii.xl, topEnd = InfRadii.xl)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top, unbounded = true)
            .onSizeChanged { drag.height = it.height.toFloat() }
            .infPanelSurface(shape, blurred = LocalInfBlurred.current)
            .nestedScroll(drag.connection)
            .infSwipeUp(drag.swipe)
    ) {
        val headerBlock: @Composable () -> Unit = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = InfSpacing.s5, end = InfSpacing.s5, bottom = InfSpacing.s4),
                verticalArrangement = Arrangement.spacedBy(InfSpacing.s4)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = InfSpacing.s2)
                        .size(DsMetrics.sheetHandleWidth, DsMetrics.sheetHandleHeight)
                        .clip(InfShapes.pill)
                        .background(colors.border)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    leading?.invoke()
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s1),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = if (onTitleClick != null) Modifier.clip(InfShapes.md).clickable(onClick = onTitleClick) else Modifier
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            if (onTitleClick != null) {
                                Icon(
                                    imageVector = Lucide.ChevronDown,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(DsMetrics.actionIconSize)
                                )
                            }
                        }
                    }
                    trailing?.invoke()
                }
                header?.invoke(this)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DsMetrics.hairlineThickness)
                    .background(colors.hairline)
            )
        }
        Column(modifier = Modifier.infParallaxLayer().fillMaxWidth().heightIn(max = maxHeight)) {
            val haze = rememberInfHazeArea()
            val density = LocalDensity.current
            var headerHeight by remember { mutableStateOf(0.dp) }
            Box(modifier = Modifier.weight(1f, fill = false).fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .infHazeSource(haze)
                        .verticalScroll(rememberScrollState())
                        .padding(top = headerHeight)
                        .then(if (edgeToEdge) Modifier else Modifier.padding(bottom = bottomInset).padding(InfSpacing.s5)),
                    content = content
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
                        .infHazeBar(haze)
                ) {
                    headerBlock()
                }
            }
        }
    }
}
