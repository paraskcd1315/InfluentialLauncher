package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints

fun Modifier.rotateVertically(clockwise: Boolean = true): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints(
            minWidth = constraints.minHeight,
            maxWidth = constraints.maxHeight,
            minHeight = constraints.minWidth,
            maxHeight = constraints.maxWidth
        )
    )
    layout(placeable.height, placeable.width) {
        placeable.placeWithLayer(
            x = (placeable.height - placeable.width) / 2,
            y = (placeable.width - placeable.height) / 2
        ) {
            rotationZ = if (clockwise) QuarterTurn else -QuarterTurn
        }
    }
}

private const val QuarterTurn = 90f
