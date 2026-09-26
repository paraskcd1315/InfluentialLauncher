package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii

object InfGroupedCorners {
    fun of(
        index: Int,
        count: Int,
        outer: Dp = InfRadii.lg,
        inner: Dp = InfRadii.sm
    ): RoundedCornerShape {
        val top = if (index == 0) outer else inner
        val bottom = if (index == count - 1) outer else inner
        return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
    }
}
