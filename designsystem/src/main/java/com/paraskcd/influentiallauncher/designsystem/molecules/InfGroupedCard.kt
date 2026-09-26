package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paraskcd.influentiallauncher.designsystem.foundation.InfGroupedCorners
import com.paraskcd.influentiallauncher.designsystem.foundation.LocalInfBlurred
import com.paraskcd.influentiallauncher.designsystem.foundation.infGlassSurface

@Composable
fun InfGroupedCard(
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val blurred = LocalInfBlurred.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .infGlassSurface(InfGroupedCorners.of(index, count), specular = index == 0, strong = !blurred, panel = blurred),
        content = content
    )
}
