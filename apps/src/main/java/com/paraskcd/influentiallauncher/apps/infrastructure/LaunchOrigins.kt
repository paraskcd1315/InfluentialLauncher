package com.paraskcd.influentiallauncher.apps.infrastructure

import android.app.ActivityOptions
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import kotlin.math.roundToInt

object LaunchOrigins {
    fun scaleUp(view: View, boundsInWindow: RectF): LaunchOrigin = scaleUp(
        view = view,
        left = boundsInWindow.left.roundToInt(),
        top = boundsInWindow.top.roundToInt(),
        width = boundsInWindow.width().roundToInt(),
        height = boundsInWindow.height().roundToInt()
    )

    fun scaleUp(view: View, left: Int, top: Int, width: Int, height: Int): LaunchOrigin {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val screenLeft = location[0] + left
        val screenTop = location[1] + top
        return LaunchOrigin(
            bounds = Rect(screenLeft, screenTop, screenLeft + width, screenTop + height),
            options = ActivityOptions.makeScaleUpAnimation(view, left, top, width, height).toBundle()
        )
    }
}
