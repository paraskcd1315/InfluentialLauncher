// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.organisms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.IntSize
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.foundation.infClickableQuiet
import com.paraskcd.influentiallauncher.designsystem.molecules.InfGroupedCard
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfAccordionCard(
    index: Int,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    toggleLabel: String,
    modifier: Modifier = Modifier,
    shape: Shape = InfGroupedCorners.of(index, count),
    headerPadding: PaddingValues = PaddingValues(horizontal = InfSpacing.s4, vertical = InfSpacing.s3),
    contentPadding: PaddingValues = PaddingValues(horizontal = InfSpacing.s4, vertical = InfSpacing.s3),
    header: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = InfTheme.colors
    val motion = tween<Float>(durationMillis = InfMotion.durAutoHeightMs, easing = InfMotion.easeIos)
    val size = tween<IntSize>(durationMillis = InfMotion.durAutoHeightMs, easing = InfMotion.easeIos)
    val turn by animateFloatAsState(
        targetValue = if (expanded) DsMetrics.accordionChevronOpenDegrees else 0f,
        animationSpec = motion,
        label = DsMetrics.accordionChevronLabel
    )
    InfGroupedCard(index = index, count = count, modifier = modifier, shape = shape) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DsMetrics.accordionHeaderMinHeight)
                .infClickableQuiet(onToggle)
                .padding(headerPadding)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(InfSpacing.s3),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                content = header
            )
            Icon(
                imageVector = Lucide.ChevronDown,
                contentDescription = toggleLabel,
                tint = colors.textTertiary,
                modifier = Modifier
                    .size(DsMetrics.accordionChevron)
                    .rotate(turn)
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = size, expandFrom = Alignment.Top) + fadeIn(animationSpec = motion),
            exit = shrinkVertically(animationSpec = size, shrinkTowards = Alignment.Top) + fadeOut(animationSpec = motion)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DsMetrics.hairlineThickness)
                        .background(colors.glassBorder)
                )
                Column(modifier = Modifier.fillMaxWidth().padding(contentPadding), content = content)
            }
        }
    }
}
