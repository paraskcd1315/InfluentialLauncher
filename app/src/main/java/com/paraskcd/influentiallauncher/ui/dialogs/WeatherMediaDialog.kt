package com.paraskcd.influentiallauncher.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.influentiallauncher.R
import com.paraskcd.influentiallauncher.ui.components.dialog_comps.WeatherMediaWidget
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.utils.isWindowBlurSupported
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WeatherMediaDialog {
    private var dialog: Dialog? = null
    private var CORNER = RoundedCornerShape(24.dp)
    private const val GAP_PX = 120
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

    fun showOrUpdate(
        context: Context
    ) {
        val activity = context as? ComponentActivity ?: return

        val supportsBlur = context.isWindowBlurSupported()

        if (isShowing()) return

        if (dialog == null) {
            dialog = Dialog(activity, R.style.Theme_DockWindow).apply {
                val compose = ComposeView(activity).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setViewTreeLifecycleOwner(activity)
                    setViewTreeViewModelStoreOwner(activity)
                    setViewTreeSavedStateRegistryOwner(activity)

                    setContent {
                        InfluentialLauncherTheme {
                            val visible by DockDialog.visibleFlow.collectAsState()
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                            ) {
                                WeatherMediaWidget(
                                    modifier = Modifier
                                        .clip(CORNER)
                                        .background(if (supportsBlur) MaterialTheme.colorScheme.background.copy(0.5f) else MaterialTheme.colorScheme.background.copy(0.9f))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                            CORNER
                                        )
                                )
                            }
                        }
                    }
                }

                setContentView(compose)
                window?.let {
                    val dm: DisplayMetrics = activity.resources.displayMetrics
                    val desiredWidth = (dm.widthPixels * 0.8f).toInt()

                    it.setLayout(desiredWidth, ViewGroup.LayoutParams.MATCH_PARENT)
                    it.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                    it.setBackgroundDrawableResource(R.drawable.dock_window_bg)
                    it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                    it.decorView.post { runCatching { it.setBackgroundBlurRadius(100) } }

                    it.decorView.viewTreeObserver.addOnGlobalLayoutListener {
                        updateDialogHeight()
                    }

                    ensureYOffsetWithDock(it, activity)
                }

                setCancelable(false)
                setOnKeyListener { _, _, _ -> false }
                show()
            }
        }
    }

    fun close() {
        dialog?.dismiss()
        dialog = null
    }

    private fun ensureYOffsetWithDock(wmWindow: Window, activity: ComponentActivity) {
        val dm: DisplayMetrics = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.8f).toInt()

        fun applyOffsetIfPossible(): Boolean {
            val dockY = DockDialog.getDockYOffset() ?: 0
            val dockH = DockDialog.getDockHeight()
            return if (dockH != null && dockH > 0) {
                val yOffset = dockY + dockH + GAP_PX
                wmWindow.attributes = wmWindow.attributes.apply { y = yOffset }
                wmWindow.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                true
            } else {
                false
            }
        }

        if (applyOffsetIfPossible()) return

        val dockView = DockDialog.getDecorView() ?: return
        val vto = dockView.viewTreeObserver
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (applyOffsetIfPossible()) {
                    if (vto.isAlive) vto.removeOnGlobalLayoutListener(this)
                    else dockView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        }
        vto.addOnGlobalLayoutListener(listener)
    }

    private val _dialogHeight = MutableStateFlow(0)
    val dialogHeightFlow: StateFlow<Int> = _dialogHeight.asStateFlow()

    private fun updateDialogHeight() {
        _dialogHeight.value = getDialogHeight() ?: 0
    }

    fun getDialogHeight(): Int? = dialog?.window?.decorView?.height?.takeIf { it > 0 }
    fun getDialogYOffset(): Int? = dialog?.window?.attributes?.y
    fun getDecorView(): View? = dialog?.window?.decorView
}