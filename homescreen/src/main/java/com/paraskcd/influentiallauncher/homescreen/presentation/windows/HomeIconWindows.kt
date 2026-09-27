package com.paraskcd.influentiallauncher.homescreen.presentation.windows

import android.graphics.Bitmap
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.designsystem.atoms.InfAsyncIcon
import com.paraskcd.influentiallauncher.homescreen.presentation.state.HomeIconSpots
import com.paraskcd.influentiallauncher.homescreen.presentation.state.IconSpot
import com.paraskcd.influentiallauncher.windowing.presentation.InfWindow

@Composable
fun HomeIconWindows(
    spots: HomeIconSpots,
    visible: Boolean,
    parallaxShift: Dp,
    loadIcon: suspend (AppId, Int) -> Bitmap?
) {
    val density = LocalDensity.current
    val screenWidth = LocalWindowInfo.current.containerSize.width
    val last = remember { arrayOf(emptyList<IconSpot>()) }
    val placed = if (visible) {
        spots.spots.values.filter { it.bounds.right > 0f && it.bounds.left < screenWidth }.also { last[0] = it }
    } else {
        last[0]
    }
    placed.forEach { spot ->
        key(spot.app.id.key) {
            val size = with(density) { spot.bounds.width.toDp() }
            InfWindow(
                cornerRadius = size / 2,
                onDismissRequest = {},
                gravity = Gravity.TOP or Gravity.START,
                offsetX = with(density) { spot.bounds.left.toDp() },
                offsetY = with(density) { spot.bounds.top.toDp() },
                visible = visible,
                touchable = false,
                animated = false,
                elevation = 0.dp,
                parallaxShift = parallaxShift,
                onShownChange = { spots.setShown(spot.app.id.key, it) }
            ) {
                InfAsyncIcon(
                    key = spot.app.id.key,
                    size = size,
                    load = { loadIcon(spot.app.id, it) },
                    version = loadIcon
                )
            }
        }
    }
}
