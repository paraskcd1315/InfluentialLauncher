package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onValueChangeFinished: () -> Unit = {}
) {
    val colors = InfTheme.colors
    val change by rememberUpdatedState(onValueChange)
    val finished by rememberUpdatedState(onValueChangeFinished)
    val position = value.coerceIn(0f, 1f)
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(DsMetrics.sliderHeight)
            .semantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo(position, 0f..1f)
                setProgress { target ->
                    change(target.coerceIn(0f, 1f))
                    finished()
                    true
                }
            }
            .pointerInput(Unit) {
                val radius = DsMetrics.sliderThumb.toPx() / 2
                detectTapGestures { offset ->
                    change(positionAt(offset.x, size.width, radius))
                    finished()
                }
            }
            .pointerInput(Unit) {
                val radius = DsMetrics.sliderThumb.toPx() / 2
                detectHorizontalDragGestures(onDragEnd = { finished() }, onDragCancel = { finished() }) { pointer, _ ->
                    pointer.consume()
                    change(positionAt(pointer.position.x, size.width, radius))
                }
            }
            .drawBehind {
                val radius = DsMetrics.sliderThumb.toPx() / 2
                val track = DsMetrics.sliderTrack.toPx()
                val y = size.height / 2
                val start = Offset(radius, y)
                val end = Offset(size.width - radius, y)
                val thumb = Offset(radius + (size.width - radius * 2) * position, y)
                drawLine(colors.border, start, end, track, StrokeCap.Round)
                drawLine(colors.brand, start, thumb, track, StrokeCap.Round)
                drawCircle(colors.surfaceContainerHigh, radius, thumb)
                drawCircle(colors.glassBorder, radius, thumb, style = Stroke(DsMetrics.hairlineThickness.toPx()))
                drawCircle(colors.brand, radius * DsMetrics.sliderThumbCore, thumb)
            }
    )
}

private fun positionAt(x: Float, width: Int, radius: Float): Float =
    ((x - radius) / (width - radius * 2).coerceAtLeast(1f)).coerceIn(0f, 1f)
