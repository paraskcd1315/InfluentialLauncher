package com.paraskcd.influentiallauncher.apps.infrastructure

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import kotlin.math.abs
import kotlin.math.roundToInt

internal object ThemedIconRenderer {
    private const val PlainIconScale = 0.65f
    private const val GradientInnerAlpha = 0x30000000
    private const val GradientOuterAlpha = 0x60000000
    private const val RgbMask = 0x00FFFFFF
    private const val OpaqueAlpha = 200
    private const val VisibleAlpha = 24
    private const val ContrastGain = 3f
    private const val ChannelMax = 255
    private const val BadgeCoverage = 0.15f
    private const val QuantizeMask = 0x00F0F0F0
    private const val LogoCoverage = 0.01f
    private const val MinContrast = 96

    fun render(icon: Drawable, tint: Int, sizePx: Int): Bitmap {
        val adaptive = icon as? AdaptiveIconDrawable
        val layer = adaptive?.monochrome ?: adaptive?.foreground
        return if (layer != null) {
            val source = layer.mutate().toBitmap(sizePx, sizePx)
            val shape = silhouette(source)
            if (shape == null && adaptive?.monochrome == null) {
                val iconSize = (sizePx * PlainIconScale).roundToInt()
                val composite = silhouette(icon.toBitmap(iconSize, iconSize))
                onCircle(composite ?: source, tint, sizePx, if (composite != null) iconSize else sizePx)
            } else {
                onCircle(shape ?: source, tint, sizePx, sizePx)
            }
        } else {
            val iconSize = (sizePx * PlainIconScale).roundToInt()
            val source = icon.toBitmap().scale(iconSize, iconSize)
            onCircle(silhouette(source) ?: source, tint, sizePx, iconSize)
        }
    }

    private fun onCircle(shape: Bitmap, tint: Int, sizePx: Int, shapeSize: Int): Bitmap {
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        val radius = sizePx / 2f
        val circle = Paint().apply {
            isAntiAlias = true
            shader = RadialGradient(
                radius, radius, radius,
                intArrayOf(tint and RgbMask or GradientInnerAlpha, tint and RgbMask or GradientOuterAlpha),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(radius, radius, radius, circle)
        val offset = (sizePx - shapeSize) / 2f
        val tinted = Paint().apply {
            isAntiAlias = true
            colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
        }
        canvas.drawBitmap(shape, offset, offset, tinted)
        return bitmap
    }

    private fun silhouette(source: Bitmap): Bitmap? {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)
        val opaque = pixels.filter { Color.alpha(it) >= OpaqueAlpha }
        if (opaque.isEmpty()) return source
        val colours = opaque.groupingBy { it and QuantizeMask }.eachCount()
        val (base, baseCount) = colours.maxBy { it.value }
        if (baseCount < pixels.size * BadgeCoverage) return source
        val backgroundRed = Color.red(base)
        val backgroundGreen = Color.green(base)
        val backgroundBlue = Color.blue(base)
        val contrasting = opaque.count { pixel ->
            abs(Color.red(pixel) - backgroundRed) + abs(Color.green(pixel) - backgroundGreen) + abs(Color.blue(pixel) - backgroundBlue) >= MinContrast
        }
        if (contrasting < opaque.size * LogoCoverage) return source
        var visible = 0
        for (index in pixels.indices) {
            val pixel = pixels[index]
            val distance = abs(Color.red(pixel) - backgroundRed) + abs(Color.green(pixel) - backgroundGreen) + abs(Color.blue(pixel) - backgroundBlue)
            val alpha = (distance * ContrastGain).roundToInt().coerceAtMost(ChannelMax) * Color.alpha(pixel) / ChannelMax
            if (alpha >= VisibleAlpha) visible++
            pixels[index] = if (alpha < VisibleAlpha) Color.TRANSPARENT else Color.argb(alpha, ChannelMax, ChannelMax, ChannelMax)
        }
        if (visible < pixels.size * LogoCoverage) return null
        return createBitmap(width, height).apply { setPixels(pixels, 0, width, 0, 0, width, height) }
    }
}
