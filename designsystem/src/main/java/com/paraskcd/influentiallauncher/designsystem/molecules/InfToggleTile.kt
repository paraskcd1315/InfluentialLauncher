package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfToggleTile(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onMore: (() -> Unit)? = null,
    moreDescription: String? = null
) {
    val colors = InfTheme.colors
    val fill by animateColorAsState(
        targetValue = if (checked) colors.brand else colors.glassBg,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "toggleFill"
    )
    val tint = if (checked) Color.White else colors.textPrimary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(InfSpacing.s2),
        modifier = modifier.alpha(if (enabled) 1f else DsMetrics.disabledAlpha)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(DsMetrics.toggleTileHeight)
                .clip(InfShapes.md)
                .background(fill)
                .border(InfGlass.borderWidth, if (checked) Color.Transparent else colors.glassBorder, InfShapes.md)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .combinedClickable(
                        role = Role.Switch,
                        onClickLabel = label,
                        onLongClick = onLongClick,
                        onClick = onToggle
                    )
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(DsMetrics.toggleTileGlyph))
            }
            if (onMore != null) {
                Box(
                    modifier = Modifier
                        .width(DsMetrics.hairlineThickness)
                        .fillMaxHeight()
                        .background(if (checked) Color.White.copy(alpha = MoreDividerAlpha) else colors.hairline)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(DsMetrics.toggleTileMoreWidth)
                        .fillMaxHeight()
                        .clickable(onClickLabel = moreDescription, onClick = onMore)
                ) {
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = moreDescription,
                        tint = tint,
                        modifier = Modifier.size(DsMetrics.toggleTileGlyph)
                    )
                }
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private const val MoreDividerAlpha = 0.3f
