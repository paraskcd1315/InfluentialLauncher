package com.paraskcd.influentiallauncher.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.graphics.Shape
import com.paraskcd.influentiallauncher.designsystem.theme.InfGlass
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Stable
class InfBackdrop internal constructor(internal val state: HazeState)

val LocalInfBackdrop = staticCompositionLocalOf<InfBackdrop?> { null }

@Composable
fun rememberInfBackdrop(): InfBackdrop {
    val state = rememberHazeState()
    return remember(state) { InfBackdrop(state) }
}

fun Modifier.infBackdropSource(backdrop: InfBackdrop): Modifier = hazeSource(backdrop.state)

@Composable
fun Modifier.infBackdropBlur(shape: Shape): Modifier {
    val backdrop = LocalInfBackdrop.current ?: return this
    return hazeBlur(
        input = HazeInput.Sources(backdrop.state),
        style = HazeBlurStyle {
            blurRadius(InfGlass.backdropBlur)
            backgroundColor(Color.Transparent)
            noiseFactor(0f)
            blurredEdgeTreatment(BlurredEdgeTreatment(shape))
        }
    )
}
