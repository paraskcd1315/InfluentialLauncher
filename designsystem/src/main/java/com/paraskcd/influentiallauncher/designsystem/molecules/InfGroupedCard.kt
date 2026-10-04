// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfBlurred
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface
import com.paraskcd.influentiallauncher.designsystem.foundation.infParallaxLayer

@Composable
fun InfGroupedCard(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    shape: Shape = InfGroupedCorners.of(index, count),
    content: @Composable ColumnScope.() -> Unit
) {
    val blurred = LocalInfBlurred.current
    Column(
        modifier = modifier
            .infParallaxLayer()
            .fillMaxWidth()
            .infGlassSurface(shape, specular = index == 0, strong = !blurred, panel = blurred),
        content = content
    )
}
