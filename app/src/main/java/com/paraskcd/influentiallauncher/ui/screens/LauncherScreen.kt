package com.paraskcd.influentiallauncher.ui.screens

import android.app.ActivityOptions
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paraskcd.influentiallauncher.dialogs.DockDialog
import com.paraskcd.influentiallauncher.ui.components.ClockHeader
import com.paraskcd.influentiallauncher.ui.components.Statusbar.Statusbar
import com.paraskcd.influentiallauncher.viewmodels.LauncherItemsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LauncherScreen(viewModel: LauncherItemsViewModel = hiltViewModel()) {
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

    val dockHeightPx = DockDialog.getDockHeight()?.toFloat() ?: 0f
    val dockHeightDp = with(density) { dockHeightPx.toDp() }

    var downAccum by remember { mutableFloatStateOf(0f) }
    var upAccum by remember { mutableFloatStateOf(0f) }

    val statusBarTop = WindowInsets
        .statusBarsIgnoringVisibility
        .asPaddingValues()
        .calculateTopPadding()

    var hiddenDockThisDrag by remember { mutableStateOf(false) }

    val progress by remember { derivedStateOf { (blur.value / 100f).coerceIn(0f, 1f) } }
    val animatedProgress by animateFloatAsState(targetValue = progress)
    val minScale = 0.92f
    val minAlpha = 0.6f
    val scale = 1f + (minScale - 1f) * animatedProgress
    val contentAlpha = 1f + (minAlpha - 1f) * animatedProgress

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal)
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
                                    val opened = viewModel.openNotifications()
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
                modifier = Modifier
                    .padding(top = statusBarTop + 32.dp, start = 48.dp, end = 48.dp, bottom = dockHeightDp + 64.dp)
                    .fillMaxSize()
                    .scale(scale),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ClockHeader()
                Column {  }
                Statusbar(
                    context = context,
                    viewModel = viewModel
                )
            }
        }
    }
}