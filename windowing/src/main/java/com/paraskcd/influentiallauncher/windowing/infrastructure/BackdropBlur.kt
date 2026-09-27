package com.paraskcd.influentiallauncher.windowing.infrastructure

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Method

object BackdropBlur {
    private const val LogTag = "BackdropBlur"
    private const val ViewSignature = "Landroid/view/View;"
    private const val ViewRootSignature = "Landroid/view/ViewRootImpl;"
    private const val DrawableSignature = "Lcom/android/internal/graphics/drawable/BackgroundBlurDrawable;"
    private const val ViewRootClass = "android.view.ViewRootImpl"
    private const val DrawableClass = "com.android.internal.graphics.drawable.BackgroundBlurDrawable"

    private class Api(
        val viewRoot: Method,
        val create: Method,
        val blurRadius: Method,
        val cornerRadius: Method,
        val color: Method
    )

    private val api: Api? by lazy {
        runCatching {
            HiddenApiBypass.addHiddenApiExemptions(ViewSignature, ViewRootSignature, DrawableSignature)
            val drawable = Class.forName(DrawableClass)
            Api(
                viewRoot = View::class.java.getMethod("getViewRootImpl"),
                create = Class.forName(ViewRootClass).getMethod("createBackgroundBlurDrawable"),
                blurRadius = drawable.getMethod("setBlurRadius", Int::class.javaPrimitiveType),
                cornerRadius = drawable.getMethod("setCornerRadius", Float::class.javaPrimitiveType),
                color = drawable.getMethod("setColor", Int::class.javaPrimitiveType)
            )
        }
            .onFailure { Log.w(LogTag, "backdrop blur unavailable", it) }
            .getOrNull()
    }

    fun create(view: View, blurRadiusPx: Int, colorArgb: Int): Drawable? {
        val calls = api ?: return null
        return runCatching {
            val root = calls.viewRoot.invoke(view) ?: return null
            (calls.create.invoke(root) as Drawable).also {
                calls.blurRadius.invoke(it, blurRadiusPx)
                calls.color.invoke(it, colorArgb)
            }
        }
            .onFailure { Log.w(LogTag, "backdrop blur failed", it) }
            .getOrNull()
    }

    fun setCornerRadius(drawable: Drawable, radiusPx: Float) {
        val calls = api ?: return
        runCatching { calls.cornerRadius.invoke(drawable, radiusPx) }
            .onFailure { Log.w(LogTag, "backdrop corner failed", it) }
    }
}
