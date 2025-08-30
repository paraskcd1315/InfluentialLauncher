package com.paraskcd.influentiallauncher.ui.theme.screens

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.paraskcd.influentiallauncher.dialogs.DockDialog
import com.paraskcd.influentiallauncher.services.SystemActionsService
import com.paraskcd.influentiallauncher.ui.theme.components.ClockHeader
import com.paraskcd.influentiallauncher.utls.openAccessibilityServiceSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LauncherScreen() {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val blur = remember { Animatable(0f) }

    LaunchedEffect(blur.value) {
        activity?.window?.setBackgroundBlurRadius(blur.value.toInt())
    }

    LaunchedEffect(Unit) {
        DockDialog.showOrUpdate(context = context)
    }

    val triggerDownPx = with(density) { 220.dp.toPx() }
    val triggerUpPx = with(density) { 220.dp.toPx() }

    var downAccum by remember { mutableFloatStateOf(0f) }
    var upAccum by remember { mutableFloatStateOf(0f) }

    val statusBarTop = WindowInsets
        .statusBarsIgnoringVisibility
        .asPaddingValues()
        .calculateTopPadding()

    Scaffold(
        containerColor = Color.Transparent
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            downAccum = 0f
                            upAccum = 0f
                        },
                        onVerticalDrag = { _, dy ->
                            if (dy > 0f) {
                                // Deslizar hacia abajo -> notificaciones
                                downAccum = (downAccum + dy).coerceAtLeast(0f)
                                upAccum = 0f
                            } else if (dy < 0f) {
                                // Deslizar hacia arriba -> SpotlightSearch
                                upAccum = (upAccum + -dy).coerceAtLeast(0f)
                                downAccum = 0f
                            }
                            val pDown = (downAccum / triggerDownPx).coerceIn(0f, 1f)
                            val pUp = (upAccum / triggerUpPx).coerceIn(0f, 1f)
                            val p = maxOf(pDown, pUp)
                            scope.launch { blur.snapTo(p * 100f) }
                        },
                        onDragEnd = {
                            val pDown = (downAccum / triggerDownPx).coerceIn(0f, 1f)
                            val pUp = (upAccum / triggerUpPx).coerceIn(0f, 1f)

                            when {
                                // Abrir SpotlightSearch al deslizar hacia arriba
                                pUp >= 0.7f -> scope.launch {
                                    blur.animateTo(100f, tween(120))
                                    val intent = Intent().apply {
                                        setClassName(
                                            "com.paraskcd.spotlightsearch",
                                            "com.paraskcd.spotlightsearch.MainActivity"
                                        )
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    // Suaviza la transición: fade mientras mantenemos el blur
                                    val opts = ActivityOptions.makeCustomAnimation(
                                        context,
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out
                                    )
                                    context.startActivity(intent, opts.toBundle())
                                    // Mantener blur un instante para evitar el “salto”
                                    delay(220)
                                    blur.snapTo(0f)
                                    activity?.window?.setBackgroundBlurRadius(0)
                                }

                                // Intentar expandir notificaciones al deslizar hacia abajo
                                pDown >= 0.7f -> scope.launch {
                                    blur.animateTo(100f, tween(100))
                                    val opened = SystemActionsService.openNotifications()
                                    if (!opened) {
                                        context.openAccessibilityServiceSettings()
                                    }
                                    delay(if (opened) 220 else 120)
                                    blur.animateTo(0f, tween(150))
                                    activity?.window?.setBackgroundBlurRadius(0)
                                }

                                else -> scope.launch { blur.animateTo(0f, tween(150)) }
                            }
                        },
                        onDragCancel = {
                            scope.launch { blur.animateTo(0f, tween(150)) }
                        }
                    )
                }
        ) {
            // Scrim sutil ligado al blur (opcional)
            val scrimAlpha = (blur.value / 100f) * 0.15f
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = scrimAlpha))
            )

            // Tu contenido principal debajo
            Column(
                modifier = Modifier.padding(top = statusBarTop, start = 24.dp, end = 24.dp)
            ) {
                ClockHeader()
            }
        }
    }
}