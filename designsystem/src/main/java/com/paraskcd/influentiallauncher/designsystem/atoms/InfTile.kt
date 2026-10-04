// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfTile(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    selected: Boolean = false,
    size: Dp = DsMetrics.tileSize,
    shape: Shape = InfShapes.md,
    highlight: Color = InfTheme.colors.textPrimary,
    content: @Composable BoxScope.() -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) DsMetrics.pressScale else 1f,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "tileScale"
    )
    val fillAlpha = when {
        pressed -> DsMetrics.tilePressedAlpha
        selected -> DsMetrics.tileSelectedAlpha
        else -> 0f
    }
    val fill by animateColorAsState(
        targetValue = highlight.copy(alpha = fillAlpha),
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "tileFill"
    )
    Box(
        modifier = modifier
            .infParallaxLayer()
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(fill)
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClickLabel = contentDescription,
                onLongClick = onLongClick,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content
    )
}
