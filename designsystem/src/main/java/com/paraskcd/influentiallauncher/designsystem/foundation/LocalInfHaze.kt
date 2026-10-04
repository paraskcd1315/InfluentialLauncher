// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Stable
class InfHazeArea {
    val state = HazeState()
    internal val holes = mutableStateMapOf<Any, Pair<Rect, Boolean>>()
    internal var origin by mutableStateOf(Offset.Zero)
}

@Composable
fun rememberInfHazeArea(): InfHazeArea = remember { InfHazeArea() }

val LocalInfHaze = staticCompositionLocalOf<InfHazeArea?> { null }

fun Modifier.infHazeSource(area: InfHazeArea): Modifier = this
    .onGloballyPositioned { area.origin = it.positionInWindow() }
    .drawWithContent {
        if (area.holes.isEmpty()) {
            drawContent()
            return@drawWithContent
        }
        val cut = Path()
        area.holes.values.forEach { (hole, rounded) ->
            val local = hole.translate(-area.origin)
            cut.addRoundRect(RoundRect(local, if (rounded) CornerRadius(local.height / 2f) else CornerRadius.Zero))
        }
        clipPath(cut, ClipOp.Difference) { this@drawWithContent.drawContent() }
    }
    .hazeSource(area.state)

internal fun Modifier.infHazeHole(area: InfHazeArea, key: Any, rounded: Boolean = true): Modifier =
    onGloballyPositioned { area.holes[key] = it.boundsInWindow() to rounded }
