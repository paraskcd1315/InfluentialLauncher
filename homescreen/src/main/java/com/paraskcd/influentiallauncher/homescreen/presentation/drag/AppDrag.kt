package com.paraskcd.influentiallauncher.homescreen.presentation.drag

import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.view.DragEvent
import android.view.View
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp

enum class DragSource { Home, Taskbar }

data class AppDragPayload(val source: DragSource, val app: LauncherApp)

object AppDrag {
    private const val Label = "influential-app"

    fun start(view: View, payload: AppDragPayload, icon: Bitmap?, sizePx: Int): Boolean =
        view.startDragAndDrop(
            ClipData.newPlainText(Label, payload.app.id.key),
            IconShadow(icon, sizePx),
            payload,
            0
        )

    fun payloadOf(event: DragEvent): AppDragPayload? = event.localState as? AppDragPayload

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
