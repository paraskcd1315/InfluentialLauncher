package com.paraskcd.influentiallauncher.windowing.infrastructure

import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object DialogWindowSetup {
    fun configure(
        window: Window,
        widthPx: Int,
        heightPx: Int,
        gravity: Int,
        offsetXPx: Int,
        offsetYPx: Int,
        cornerRadiusPx: Float,
        elevationPx: Float,
        shadowAlpha: Float,
        fullScreen: Boolean
    ) {
        window.decorView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadiusPx)
                outline.alpha = shadowAlpha
            }
        }
        window.setElevation(elevationPx)
        window.setBackgroundDrawable(
            GradientDrawable().apply {
                cornerRadius = cornerRadiusPx
                setColor(Color.TRANSPARENT)
            }
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0f)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or
                WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
        )
        if (fullScreen) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.isNavigationBarContrastEnforced = false
        }
        window.setGravity(gravity)
        window.setLayout(widthPx, heightPx)
        window.attributes = window.attributes.apply {
            x = offsetXPx
            y = offsetYPx
            windowAnimations = 0
            if (fullScreen) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                fitInsetsTypes = 0
            }
        }
    }

    fun place(window: Window, offsetXPx: Int, offsetYPx: Int, alpha: Float) {
        val attributes = window.attributes
        if (attributes.x == offsetXPx && attributes.y == offsetYPx && attributes.alpha == alpha) return
        window.attributes = attributes.apply {
            x = offsetXPx
            y = offsetYPx
            this.alpha = alpha
        }
    }

    fun setVisible(window: Window, visible: Boolean, focusable: Boolean, touchable: Boolean = true) {
        window.decorView.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        setFlag(window, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, on = !(visible && touchable))
        setFlag(window, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, on = !(visible && focusable))
    }

    private fun setFlag(window: Window, flag: Int, on: Boolean) {
        val current = window.attributes.flags and flag != 0
        if (current == on) return
        if (on) window.addFlags(flag) else window.clearFlags(flag)
    }

    fun setStatusBar(window: Window, visible: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (visible) show(WindowInsetsCompat.Type.statusBars()) else hide(WindowInsetsCompat.Type.statusBars())
        }
    }

    fun setBlur(window: Window, radius: Int) {
        window.setBackgroundBlurRadius(radius)
    }

    fun displayWidth(window: Window): Int =
        window.windowManager.currentWindowMetrics.bounds.width()

    fun displayHeight(window: Window): Int =
        window.windowManager.currentWindowMetrics.bounds.height()
}
