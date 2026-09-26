package com.paraskcd.influentiallauncher.designsystem.atoms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfShapes
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = InfTheme.colors
    val track by animateColorAsState(
        targetValue = if (checked) colors.brand else colors.surfaceContainerHigh,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "track"
    )
    val inset = (DsMetrics.switchTrackHeight - DsMetrics.switchKnobSize) / 2
    val knobOffset by animateDpAsState(
        targetValue = if (checked) DsMetrics.switchTrackWidth - DsMetrics.switchKnobSize - inset else inset,
        animationSpec = tween(InfMotion.durMorphMs, easing = InfMotion.easeIos),
        label = "knob"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minWidth = DsMetrics.touchTargetMin, minHeight = DsMetrics.touchTargetMin)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Switch
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .width(DsMetrics.switchTrackWidth)
                .height(DsMetrics.switchTrackHeight)
                .clip(InfShapes.pill)
                .background(track)
                .border(InfGlass.borderWidth, if (checked) colors.brand else colors.border, InfShapes.pill)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = knobOffset)
                    .size(DsMetrics.switchKnobSize)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
