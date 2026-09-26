package com.paraskcd.influentiallauncher.apps.infrastructure

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
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

internal object ThemedIconRenderer {
    private const val PlainIconScale = 0.65f
    private const val GradientInnerAlpha = 0x30000000
    private const val GradientOuterAlpha = 0x60000000
    private const val RgbMask = 0x00FFFFFF
    private const val ChannelMax = 255f
    private const val LumaRed = 0.299f
    private const val LumaGreen = 0.587f
    private const val LumaBlue = 0.114f

    fun render(icon: Drawable, tint: Int, sizePx: Int): Bitmap {
        val monochrome = (icon as? AdaptiveIconDrawable)?.monochrome
        return if (monochrome != null) monochromeOnCircle(monochrome, tint, sizePx) else colorizedOnCircle(icon, tint, sizePx)
    }

    private fun monochromeOnCircle(monochrome: Drawable, tint: Int, sizePx: Int): Bitmap {
        val (bitmap, canvas) = circle(tint, sizePx)
        val tinted = monochrome.mutate().apply { colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN) }
        canvas.drawBitmap(tinted.toBitmap(sizePx, sizePx), 0f, 0f, null)
        return bitmap
    }

    private fun colorizedOnCircle(icon: Drawable, tint: Int, sizePx: Int): Bitmap {
        val (bitmap, canvas) = circle(tint, sizePx)
        val iconSize = (sizePx * PlainIconScale).toInt()
        val offset = (sizePx - iconSize) / 2f
        val red = Color.red(tint) / ChannelMax
        val green = Color.green(tint) / ChannelMax
        val blue = Color.blue(tint) / ChannelMax
        val colorize = ColorMatrix(
            floatArrayOf(
                LumaRed * red, LumaGreen * red, LumaBlue * red, 0f, 0f,
                LumaRed * green, LumaGreen * green, LumaBlue * green, 0f, 0f,
                LumaRed * blue, LumaGreen * blue, LumaBlue * blue, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorize)
            isAntiAlias = true
        }
        canvas.drawBitmap(icon.toBitmap().scale(iconSize, iconSize), offset, offset, paint)
        return bitmap
    }

    private fun circle(tint: Int, sizePx: Int): Pair<Bitmap, Canvas> {
        val bitmap = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bitmap)
        val radius = sizePx / 2f
        val paint = Paint().apply {
            isAntiAlias = true
            shader = RadialGradient(
                radius, radius, radius,
                intArrayOf(tint and RgbMask or GradientInnerAlpha, tint and RgbMask or GradientOuterAlpha),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(radius, radius, radius, paint)
        return bitmap to canvas
    }
}
