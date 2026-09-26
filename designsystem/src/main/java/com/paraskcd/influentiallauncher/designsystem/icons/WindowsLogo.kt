package com.paraskcd.influentiallauncher.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val WindowsLogo: ImageVector by lazy {
    ImageVector.Builder(
        name = "WindowsLogo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(2f, 2f); lineTo(11.25f, 2f); lineTo(11.25f, 11.25f); lineTo(2f, 11.25f); close()
        }
        path(fill = SolidColor(Color.Black), fillAlpha = 0.7f) {
            moveTo(12.75f, 2f); lineTo(22f, 2f); lineTo(22f, 11.25f); lineTo(12.75f, 11.25f); close()
        }
        path(fill = SolidColor(Color.Black), fillAlpha = 0.7f) {
            moveTo(2f, 12.75f); lineTo(11.25f, 12.75f); lineTo(11.25f, 22f); lineTo(2f, 22f); close()
        }
        path(fill = SolidColor(Color.Black), fillAlpha = 0.7f) {
            moveTo(12.75f, 12.75f); lineTo(22f, 12.75f); lineTo(22f, 22f); lineTo(12.75f, 22f); close()
        }
    }.build()
}
