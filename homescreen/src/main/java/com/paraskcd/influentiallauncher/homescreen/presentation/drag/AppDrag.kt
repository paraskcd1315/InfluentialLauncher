// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.homescreen.presentation.drag

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.DragEvent
import android.view.View
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

enum class DragSource { Home, Taskbar }

data class AppDragPayload(val source: DragSource, val app: LauncherApp)

object AppDrag {
    private const val Label = "influential-app"

    @Volatile
    private var active: AppDragPayload? = null

    fun start(view: View, payload: AppDragPayload, icon: Bitmap?, sizePx: Int): Boolean {
        active = payload
        val started = view.startDragAndDrop(
            ClipData.newPlainText(Label, payload.app.id.key),
            IconShadow(icon, sizePx),
            payload,
            crossWindowFlags()
        )
        if (!started) active = null
        return started
    }

    fun payloadOf(event: DragEvent): AppDragPayload? {
        (event.localState as? AppDragPayload)?.let { return it }
        if (event.clipDescription?.label != Label) return null
        return active
    }

    private fun crossWindowFlags(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) View.DRAG_FLAG_GLOBAL_SAME_APPLICATION else View.DRAG_FLAG_GLOBAL

    private class IconShadow(private val icon: Bitmap?, private val sizePx: Int) : View.DragShadowBuilder() {
        override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
            outShadowSize.set(sizePx, sizePx)
            outShadowTouchPoint.set(sizePx / 2, sizePx / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            val bitmap = icon ?: return
            canvas.drawBitmap(bitmap, null, Rect(0, 0, sizePx, sizePx), null)
        }
    }
}
