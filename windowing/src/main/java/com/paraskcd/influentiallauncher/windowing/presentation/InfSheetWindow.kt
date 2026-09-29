// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.windowing.presentation

import android.view.Gravity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.designsystem.foundation.DsMetrics
import com.paraskcd.influentiallauncher.designsystem.foundation.SheetDrag
import com.paraskcd.influentiallauncher.designsystem.foundation.infClickableQuiet
import com.paraskcd.influentiallauncher.designsystem.organisms.InfBottomSheet
import com.paraskcd.influentiallauncher.designsystem.theme.InfMotion
import com.paraskcd.influentiallauncher.designsystem.theme.InfRadii
import com.paraskcd.influentiallauncher.designsystem.theme.InfSpacing
import com.paraskcd.influentiallauncher.designsystem.theme.InfTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T : Any> InfSheetWindow(
    item: T?,
    title: @Composable (T) -> String,
    onDismiss: () -> Unit,
    leading: (@Composable (T) -> Unit)? = null,
    edgeToEdge: Boolean = false,
    header: (@Composable ColumnScope.(T) -> Unit)? = null,
    onTitleClick: ((T) -> Unit)? = null,
    trailing: (@Composable (T) -> Unit)? = null,
    content: @Composable ColumnScope.(T) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val dismiss by rememberUpdatedState(onDismiss)
    val flingVelocity = with(density) { DsMetrics.sheetDismissFling.toPx() }
    val drag = remember(flingVelocity) { SheetDrag(scope, flingVelocity) { dismiss() } }
    val shown = remember { Animatable(0f) }
    var retained by remember { mutableStateOf(item) }
    var open by remember { mutableStateOf(false) }
    LaunchedEffect(item) {
        if (item != null) {
            retained = item
            open = true
            drag.reset()
            shown.animateTo(1f, tween(InfMotion.durPushMs, easing = InfMotion.easeIos))
        } else {
            open = false
            shown.animateTo(0f, tween(InfMotion.durPushMs, easing = InfMotion.easeIos))
            retained = null
        }
    }
    val current = item ?: retained ?: return
    val screenHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
    val statusTop = with(density) { WindowInsets.statusBarsIgnoringVisibility.getTop(density).toDp() }

    InfWindow(
        cornerRadius = 0.dp,
        onDismissRequest = onDismiss,
        gravity = Gravity.TOP or Gravity.START,
        visible = open,
        fullScreen = true,
        blurBehind = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InfTheme.colors.scrim)
                .infClickableQuiet(onDismiss)
        )
    }
    InfWindow(
        cornerRadius = InfRadii.xl,
        onDismissRequest = onDismiss,
        fillWidth = true,
        visible = open,
        focusable = true,
        followsTilt = false,
        dropPx = { drag.height * (1f - shown.value) + drag.offset },
        heightPx = drag.height.roundToInt().takeIf { it > 0 }
    ) {
        InfBottomSheet(
            drag = drag,
            maxHeight = screenHeight - statusTop - InfSpacing.s2,
            title = title(current),
            leading = leading?.let { { it(current) } },
            edgeToEdge = edgeToEdge,
            header = header?.let { slot -> { slot(current) } },
            onTitleClick = onTitleClick?.let { click -> { click(current) } },
            trailing = trailing?.let { slot -> { slot(current) } }
        ) {
            content(current)
        }
    }
}
