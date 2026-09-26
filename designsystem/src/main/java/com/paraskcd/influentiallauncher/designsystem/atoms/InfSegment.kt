package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfSegment(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val background by animateColorAsState(
        targetValue = if (active) colors.brandTint else Color.Transparent,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "segment"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(DsMetrics.segmentHeight)
            .clip(InfShapes.pill)
            .background(background)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
            .padding(horizontal = InfSpacing.s3)
    ) {
        Text(
            text = label,
            fontSize = DsMetrics.segmentTextSize,
            fontWeight = FontWeight.Bold,
            color = if (active) colors.brandText else colors.textSecondary,
            maxLines = 1
        )
    }
}
