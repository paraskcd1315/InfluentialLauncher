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

    fun grid(
        row: Int,
        column: Int,
        rows: Int,
        columns: Int,
        lastRowSize: Int,
        outer: Dp = InfRadii.lg,
        inner: Dp = InfRadii.sm
    ): RoundedCornerShape {
        val firstRowEnd = if (rows == 1) lastRowSize - 1 else columns - 1
        val topStart = row == 0 && column == 0
        val topEnd = row == 0 && column == firstRowEnd
        val bottomStart = row == rows - 1 && column == 0
        val bottomEnd = (row == rows - 1 && column == lastRowSize - 1) ||
            (row == rows - 2 && column == columns - 1 && lastRowSize < columns)
        return RoundedCornerShape(
            topStart = if (topStart) outer else inner,
            topEnd = if (topEnd) outer else inner,
            bottomStart = if (bottomStart) outer else inner,
            bottomEnd = if (bottomEnd) outer else inner
        )
    }
}
