// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.controlcenter.R
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ControlState
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ShellAccess
import com.paraskcd.influentiallauncher.controlcenter.presentation.utils.ControlCenterMetrics
import com.paraskcd.influentiallauncher.controlcenter.presentation.utils.ToggleVisuals
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.molecules.InfToggleTile
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun ToggleGrid(
    state: ControlState,
    onToggle: (QuickToggle) -> Unit,
    onDetails: (QuickToggle) -> Unit,
    modifier: Modifier = Modifier
) {
    val label = MaterialTheme.typography.labelMedium
    val labelHeight = with(LocalDensity.current) {
        if (label.lineHeight.isSp) label.lineHeight.toDp() else label.fontSize.toDp() * ControlCenterMetrics.lineHeightFallback
    }
    val rowHeight = DsMetrics.toggleTileHeight + InfSpacing.s2 + labelHeight
    val allRows = (ToggleVisuals.order.size + ControlCenterMetrics.columns - 1) / ControlCenterMetrics.columns
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val rows = if (constraints.hasBoundedHeight) {
            ((maxHeight - InfSpacing.s5 * 2 + InfSpacing.s4) / (rowHeight + InfSpacing.s4)).toInt().coerceIn(ControlCenterMetrics.minRows, allRows)
        } else {
            allRows
        }
        TogglePages(state = state, rows = rows, rowHeight = rowHeight, onToggle = onToggle, onDetails = onDetails)
    }
}

@Composable
private fun TogglePages(
    state: ControlState,
    rows: Int,
    rowHeight: Dp,
    onToggle: (QuickToggle) -> Unit,
    onDetails: (QuickToggle) -> Unit
) {
    val pages = ToggleVisuals.order.chunked(ControlCenterMetrics.columns * rows)
    val pager = rememberPagerState { pages.size }
    val pageHeight = rowHeight * rows + InfSpacing.s4 * (rows - 1)
    Box(modifier = Modifier.fillMaxWidth()) {
        VerticalPager(
            state = pager,
            contentPadding = PaddingValues(vertical = InfSpacing.s5),
            pageSpacing = InfSpacing.s5,
            modifier = Modifier
                .fillMaxWidth()
                .height(pageHeight + InfSpacing.s5 * 2)
        ) { page ->
            Column(
                verticalArrangement = Arrangement.spacedBy(InfSpacing.s4),
                modifier = Modifier
                    .height(pageHeight)
                    .padding(start = InfSpacing.s5, end = ControlCenterMetrics.gridEnd)
            ) {
                pages[page].chunked(ControlCenterMetrics.columns).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3)) {
                        row.forEach { toggle ->
                            val label = stringResource(ToggleVisuals.labelOf(toggle))
                            InfToggleTile(
                                icon = ToggleVisuals.iconOf(toggle),
                                label = label,
                                checked = state.toggles[toggle] == true,
                                onToggle = { onToggle(toggle) },
                                enabled = !toggle.needsShell || state.access == ShellAccess.Ready,
                                onLongClick = { onDetails(toggle) },
                                onMore = if (toggle in ToggleVisuals.withDetails) ({ onDetails(toggle) }) else null,
                                moreDescription = stringResource(R.string.controlcenter_more, label),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(ControlCenterMetrics.columns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
        if (pages.size > 1) {
            Column(
                verticalArrangement = Arrangement.spacedBy(ControlCenterMetrics.dotGap),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = InfSpacing.s3)
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(ControlCenterMetrics.dot)
                            .clip(CircleShape)
                            .background(
                                InfTheme.colors.textPrimary.copy(
                                    alpha = if (index == pager.currentPage) 1f else ControlCenterMetrics.dotIdleAlpha
                                )
                            )
                    )
                }
            }
        }
    }
}
