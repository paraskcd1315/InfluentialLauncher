package com.paraskcd.influentiallauncher.dialogs

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paraskcd.influentiallauncher.R
import com.paraskcd.influentiallauncher.ui.theme.InfluentialLauncherTheme
import com.paraskcd.influentiallauncher.ui.theme.components.StartMenu
import com.paraskcd.influentiallauncher.utls.isWindowBlurSupported
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StartMenuDialog {
    private var dialog: Dialog? = null
    private val CORNER = RoundedCornerShape(24.dp)
    private const val GAP_PX = 64

    private var blurAnimator: ValueAnimator? = null
    private var currentBlurRadius: Int = 0

    private val _isOpen = MutableStateFlow(false)
    val isOpenFlow: StateFlow<Boolean> = _isOpen.asStateFlow()

    fun showOrUpdate(
        context: Context
    ) {
        val activity = context as? ComponentActivity ?: return
        val supportsBlur = context.isWindowBlurSupported()

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
                            val (visible, setVisible) = remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                setVisible(true)
                            }

                            AnimatedVisibility(
                                visible = visible,
                                enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
                            ) {
                                StartMenu(
                                    modifier = Modifier
                                        .clip(CORNER)
                                        .background(if (supportsBlur) MaterialTheme.colorScheme.background.copy(0.5f) else MaterialTheme.colorScheme.background.copy(0.9f))
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CORNER)
                                )
                            }
                        }
                    }
                }
                setContentView(compose)
                window?.let {
                    val dm: DisplayMetrics = activity.resources.displayMetrics
                    val desiredWidth = (dm.widthPixels * 0.9f).toInt()

                    it.setLayout(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                    it.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                    it.setBackgroundDrawableResource(R.drawable.dock_window_bg)
                    it.clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    setWindowBlur(0)
                    ensureYOffsetWithDock(it, activity)
                }

                setCancelable(true)
                setCanceledOnTouchOutside(true)

                setOnDismissListener {
                    blurAnimator?.cancel()
                    blurAnimator = null
                    setWindowBlur(0)
                    currentBlurRadius = 0
                    _isOpen.value = false
                    dialog = null
                }

                show()

                animateWindowBlurTo(target = 100, duration = 250L)

                _isOpen.value = true
            }
        }
    }


    fun close() {
        dialog?.dismiss()
        dialog = null
    }

    private fun ensureYOffsetWithDock(smWindow: Window, activity: ComponentActivity) {
        val dm: DisplayMetrics = activity.resources.displayMetrics
        val desiredWidth = (dm.widthPixels * 0.9f).toInt()
        val statusBarTopInset = getStatusBarInsetTopPx(activity)

        fun applyOffsetIfPossible(): Boolean {
            val dockY = DockDialog.getDockYOffset() ?: 0
            val dockH = DockDialog.getDockHeight()
            return if (dockH != null && dockH > 0) {
                val yOffset = dockY + dockH + GAP_PX
                smWindow.attributes = smWindow.attributes.apply { y = yOffset }
                val maxHeight = (dm.heightPixels - statusBarTopInset - yOffset).coerceAtLeast(1)
                smWindow.setLayout(desiredWidth, maxHeight)
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

    private fun getStatusBarInsetTopPx(activity: ComponentActivity): Int {
        val view = activity.window?.decorView ?: return 0
        val insets = ViewCompat.getRootWindowInsets(view)
        return insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
    }

    private fun setWindowBlur(radius: Int) {
        dialog?.window?.let { win ->
            runCatching { win.setBackgroundBlurRadius(radius) }
        }
    }

    private fun animateWindowBlurTo(target: Int, duration: Long, onEnd: (() -> Unit)? = null) {
        blurAnimator?.cancel()
        val start = currentBlurRadius.coerceAtLeast(0)
        if (start == target) {
            onEnd?.invoke()
            return
        }
        blurAnimator = ValueAnimator.ofInt(start, target).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val value = anim.animatedValue as Int
                setWindowBlur(value)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }
}