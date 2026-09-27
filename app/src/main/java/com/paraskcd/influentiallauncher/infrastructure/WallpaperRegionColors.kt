package com.paraskcd.influentiallauncher.infrastructure

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/** Reports whether the wallpaper behind a screen region wants dark text. */
class WallpaperRegionColors(context: Context) {
    private val manager = WallpaperManager.getInstance(context)
    private val main = Handler(Looper.getMainLooper())

    fun observe(region: RectF, onDarkText: (Boolean) -> Unit): AutoCloseable =
        observeRegion(region, onDarkText) ?: observeWhole(onDarkText)

    private fun observeRegion(region: RectF, onDarkText: (Boolean) -> Unit): AutoCloseable? {
        val api = LocalColorsApi.instance ?: return null
        val consumer = Proxy.newProxyInstance(api.consumer.classLoader, arrayOf(api.consumer)) { self, method, args ->
            when (method.name) {
                OnColorsChanged -> {
                    val colors = args?.getOrNull(1) as? WallpaperColors
                    Log.d(LogTag, "region $region hints=${colors?.colorHints} primary=${colors?.primaryColor}")
                    main.post { onDarkText(wantsDarkText(colors)) }
                    null
                }
                "hashCode" -> System.identityHashCode(self)
                "equals" -> self === args?.getOrNull(0)
                "toString" -> "WallpaperRegionConsumer"
                else -> null
            }
        }
        return runCatching {
            api.add.invoke(manager, consumer, listOf(region), WallpaperManager.FLAG_SYSTEM)
            AutoCloseable { runCatching { api.remove.invoke(manager, consumer) } }
        }.onFailure { Log.w(LogTag, "local wallpaper colours unavailable", it) }.getOrNull()
    }

    private fun observeWhole(onDarkText: (Boolean) -> Unit): AutoCloseable {
        onDarkText(wantsDarkText(manager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)))
        val listener = WallpaperManager.OnColorsChangedListener { colors, which ->
            if (which and WallpaperManager.FLAG_SYSTEM != 0) onDarkText(wantsDarkText(colors))
        }
        manager.addOnColorsChangedListener(listener, main)
        return AutoCloseable { manager.removeOnColorsChangedListener(listener) }
    }

    private fun wantsDarkText(colors: WallpaperColors?): Boolean =
        colors != null && colors.colorHints and WallpaperColors.HINT_SUPPORTS_DARK_TEXT != 0

    private class LocalColorsApi(val consumer: Class<*>, val add: Method, val remove: Method) {
        companion object {
            val instance: LocalColorsApi? by lazy {
                runCatching {
                    HiddenApiBypass.addHiddenApiExemptions(WallpaperManagerSignature)
                    val consumer = Class.forName(ConsumerClass)
                    LocalColorsApi(
                        consumer = consumer,
                        add = WallpaperManager::class.java.getMethod(AddListener, consumer, List::class.java, Int::class.javaPrimitiveType),
                        remove = WallpaperManager::class.java.getMethod(RemoveListener, consumer)
                    )
                }.onFailure { Log.w(LogTag, "local wallpaper colours api missing", it) }.getOrNull()
            }
        }
    }

    private companion object {
        const val LogTag = "WallpaperRegion"
        const val WallpaperManagerSignature = "Landroid/app/WallpaperManager"
        const val ConsumerClass = "android.app.WallpaperManager\$LocalWallpaperColorConsumer"
        const val AddListener = "addOnColorsChangedListener"
        const val RemoveListener = "removeOnColorsChangedListener"
        const val OnColorsChanged = "onColorsChanged"
    }
}
