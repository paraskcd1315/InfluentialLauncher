package com.paraskcd.influentiallauncher.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val WindowsIcon: ImageVector
    get() {
        if (_Windows != null) return _Windows!!

        _Windows = ImageVector.Builder(
            name = "Windows11",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            val m = 1f
            val g = 0.5f
            val cell = (24f - 2f * m - g) / 2f

            val x0 = m
            val x1 = m + cell
            val x2 = x1 + g
            val x3 = x2 + cell

            val y0 = m
            val y1 = m + cell
            val y2 = y1 + g
            val y3 = y2 + cell

            // Superior izquierdo
            path(fill = SolidColor(Color.Black)) {
                moveTo(x0, y0); lineTo(x1, y0); lineTo(x1, y1); lineTo(x0, y1); close()
            }
            // Superior derecho
            path(fill = SolidColor(Color.Black.copy(alpha = 0.5f))) {
                moveTo(x2, y0); lineTo(x3, y0); lineTo(x3, y1); lineTo(x2, y1); close()
            }
            // Inferior izquierdo
            path(fill = SolidColor(Color.Black.copy(alpha = 0.5f))) {
                moveTo(x0, y2); lineTo(x1, y2); lineTo(x1, y3); lineTo(x0, y3); close()
            }
            // Inferior derecho
            path(fill = SolidColor(Color.Black.copy(alpha = 0.5f))) {
                moveTo(x2, y2); lineTo(x3, y2); lineTo(x3, y3); lineTo(x2, y3); close()
            }
        }.build()

        return _Windows!!
    }

private var _Windows: ImageVector? = null

