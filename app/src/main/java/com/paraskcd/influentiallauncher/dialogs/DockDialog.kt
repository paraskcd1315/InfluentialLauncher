package com.paraskcd.influentiallauncher.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.influentiallauncher.R
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.ui.theme.components.BottomDock
import com.paraskcd.influentiallauncher.utls.isWindowBlurSupported
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.text.toInt
import kotlin.times

object DockDialog {
    private var dialog: Dialog? = null
    private val CORNER = RoundedCornerShape(24.dp)
    private const val FLOAT_DISTANCE_DP = 48

    private val _visible = MutableStateFlow(true)
    val visibleFlow: StateFlow<Boolean> = _visible.asStateFlow()

    fun isShowing(): Boolean = dialog?.isShowing == true

    fun ensureShown(context: Context) {
        if (!isShowing()) {
            close()
            showOrUpdate(context)
        }
    }

    fun setVisible(visible: Boolean) {
        _visible.value = visible
    }

    fun show() = setVisible(true)
    fun hide() = setVisible(false)

    fun showOrUpdate(context: Context) {
        val activity = context as? ComponentActivity ?: return
        val supportsBlur = context.isWindowBlurSupported()

        if (isShowing()) return

        if (dialog == null) {
            dialog = Dialog(activity, R.style.Theme_DockWindow).apply {
                val compose = ComposeView(activity).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setViewTreeLifecycleOwner(activity)
                    setViewTreeViewModelStoreOwner(activity)
                    setViewTreeSavedStateRegistryOwner(activity)
                    setContent {
                        InfluentialLauncherTheme {
                            val visible by visibleFlow.collectAsState()
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                            ) {
                                BottomDock(
                                    modifier = Modifier
                                        .clip(CORNER)
                                        .background(
                                            if (supportsBlur) MaterialTheme.colorScheme.background.copy(
                                                0.5f
                                            ) else MaterialTheme.colorScheme.background.copy(0.9f)
                                        )
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            CORNER
                                        ),
                                    context
                                )
                            }
                        }
                    }
                }

                setContentView(compose)

                window?.let {
                    val dm: DisplayMetrics = activity.resources.displayMetrics
                    val desiredWidth = (dm.widthPixels * 0.8f).toInt()
                    val offset = (dm.density * FLOAT_DISTANCE_DP).toInt()

                    it.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                    it.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                    it.setBackgroundDrawableResource(R.drawable.dock_window_bg)
                    it.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.addFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                    it.attributes.apply { y = offset }
                    it.decorView.post { runCatching { it.setBackgroundBlurRadius(100) } }
                }

                setCancelable(false)
                setOnKeyListener { _, keyCode, _ ->
                    keyCode == android.view.KeyEvent.KEYCODE_BACK // Ignora el botón atrás
                }
                show()
            }
        }
    }

    fun close() {
        dialog?.setOnDismissListener(null)
        dialog?.dismiss()
        dialog = null
    }

    fun getDockHeight(): Int? = dialog?.window?.decorView?.height?.takeIf { it > 0 }
    fun getDockYOffset(): Int? = dialog?.window?.attributes?.y
    fun getDecorView(): View? = dialog?.window?.decorView
}