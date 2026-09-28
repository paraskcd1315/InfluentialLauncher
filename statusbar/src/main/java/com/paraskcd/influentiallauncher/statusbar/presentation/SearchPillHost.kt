// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.statusbar.presentation

import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Search
import com.paraskcd.influentiallauncher.designsystem.foundation.SwipeUp
import com.paraskcd.influentiallauncher.designsystem.foundation.infPanelSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infSwipeUp
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import com.paraskcd.influentiallauncher.statusbar.R
import com.paraskcd.influentiallauncher.statusbar.presentation.utils.StatusBarMetrics
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow
import com.paraskcd.influentiallauncher.windowing.presentation.LocalWindowBlurred

@Composable
fun SearchPillHost(
    offsetX: Dp,
    offsetY: Dp,
    visible: Boolean,
    onClick: () -> Unit,
    alpha: Float = 1f,
    swipeUp: SwipeUp? = null
) {
    InfWindow(
        cornerRadius = StatusBarMetrics.cornerRadius,
        onDismissRequest = { },
        gravity = Gravity.BOTTOM or Gravity.START,
        offsetX = offsetX,
        offsetY = offsetY,
        visible = visible,
        alpha = alpha
    ) {
        val shape = RoundedCornerShape(StatusBarMetrics.cornerRadius)
        val label = stringResource(R.string.statusbar_search)
        Row(
            horizontalArrangement = Arrangement.spacedBy(StatusBarMetrics.iconGap),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .infPanelSurface(shape, blurred = LocalWindowBlurred.current)
                .infSwipeUp(swipeUp)
                .clickable(onClickLabel = label, onClick = onClick)
                .heightIn(min = StatusBarMetrics.minHeight)
                .padding(horizontal = StatusBarMetrics.paddingHorizontal, vertical = StatusBarMetrics.paddingVertical)
        ) {
            Icon(
                imageVector = Lucide.Search,
                contentDescription = null,
                tint = InfTheme.colors.textPrimary,
                modifier = Modifier.size(StatusBarMetrics.iconSize)
            )
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = InfTheme.colors.textPrimary)
        }
    }
}
