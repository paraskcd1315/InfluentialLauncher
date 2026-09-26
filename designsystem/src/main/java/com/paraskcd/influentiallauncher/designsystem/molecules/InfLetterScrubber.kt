package com.paraskcd.influentiallauncher.designsystem.molecules

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme

@Composable
fun InfLetterScrubber(
    letters: List<Char>,
    available: Set<Char>,
    onScrub: (Char?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = InfTheme.colors
    val latestScrub by rememberUpdatedState(onScrub)
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight()
            .width(DsMetrics.scrubberWidth)
            .pointerInput(letters) {
                fun letterAt(y: Float): Char {
                    val index = (y / size.height * letters.size).toInt().coerceIn(0, letters.lastIndex)
                    return letters[index]
                }
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    latestScrub(letterAt(down.position.y))
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) break
                        if (change.positionChange().y != 0f) latestScrub(letterAt(change.position.y))
                        change.consume()
                    }
                    latestScrub(null)
                }
            }
    ) {
        letters.forEach { letter ->
            Text(
                text = letter.toString(),
                fontSize = DsMetrics.scrubberLetterSize,
                fontWeight = FontWeight.SemiBold,
                color = if (letter in available) colors.textPrimary else colors.textTertiary
            )
        }
    }
}
