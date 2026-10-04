// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.infrastructure

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Handler
import android.os.Process
import android.os.UserHandle
import android.util.Log
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import com.paraskcd.influentiallauncher.apps.domain.model.AppIconChoice
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.IconLayers
import com.paraskcd.influentiallauncher.apps.domain.model.IconStyle
import com.paraskcd.influentiallauncher.apps.domain.model.LaunchOrigin
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.IconStyleStore
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.apps.infrastructure.iconpack.IconPackRenderer
import com.paraskcd.influentiallauncher.apps.infrastructure.iconpack.IconPackSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauncherAppsInstalledApps @Inject constructor(
    @ApplicationContext private val context: Context,
    private val styleStore: IconStyleStore,
    private val iconPacks: IconPackSource
) : InstalledApps {

    private val launcherApps = context.getSystemService(LauncherApps::class.java)
    private val user: UserHandle = Process.myUserHandle()
    private val iconCache = LruCache<String, Bitmap>(IconCacheEntries)
    private val layerCache = LruCache<String, IconLayers>(IconCacheEntries)

    override val iconStyle: StateFlow<IconStyle> = styleStore.style

    override val apps: Flow<List<LauncherApp>> = callbackFlow {
        val callback = object : LauncherApps.Callback() {
            override fun onPackageRemoved(packageName: String, user: UserHandle) { trySend(readApps()) }
            override fun onPackageAdded(packageName: String, user: UserHandle) { trySend(readApps()) }
            override fun onPackageChanged(packageName: String, user: UserHandle) { trySend(readApps()) }
            override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) { trySend(readApps()) }
            override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) { trySend(readApps()) }
        }
        trySend(readApps())
        launcherApps.registerCallback(callback, Handler(context.mainLooper))
        awaitClose { launcherApps.unregisterCallback(callback) }
    }.flowOn(Dispatchers.IO)

    override fun launch(id: AppId, origin: LaunchOrigin?): Boolean = runCatching {
        launcherApps.startMainActivity(ComponentName(id.packageName, id.activityName), user, origin?.bounds, origin?.options)
    }.onFailure { Log.w(LogTag, "launch failed for ${id.key}", it) }.isSuccess

    override fun openInfo(id: AppId, sourceBounds: Rect?): Boolean = runCatching {
        launcherApps.startAppDetailsActivity(ComponentName(id.packageName, id.activityName), user, sourceBounds, null)
    }.onFailure { Log.w(LogTag, "app info failed for ${id.key}", it) }.isSuccess

    override fun uninstall(id: AppId): Boolean = runCatching {
        val intent = Intent(Intent.ACTION_DELETE, Uri.fromParts(PackageScheme, id.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }.onFailure { Log.w(LogTag, "uninstall failed for ${id.key}", it) }.isSuccess

    override fun cachedIcon(id: AppId, sizePx: Int, tint: Int?, background: Int?): Bitmap? =
        iconCache.get(cacheKeyOf(id, sizePx, tint, background, iconStyle.value))

    private fun cacheKeyOf(id: AppId, sizePx: Int, tint: Int?, background: Int?, style: IconStyle): String {
        val pack = style.iconPack?.let { "#$it#${LocalDate.now().dayOfMonth}" }.orEmpty()
        val chosen = style.choices[id]?.let { "#${it.iconPack}/${it.drawable}" }.orEmpty()
        return "${id.key}#$sizePx#${tint ?: 0}#${background ?: 0}$pack$chosen"
    }

    override suspend fun icon(id: AppId, sizePx: Int, tint: Int?, background: Int?): Bitmap? {
        val style = iconStyle.value
        val cacheKey = cacheKeyOf(id, sizePx, tint, background, style)
        iconCache.get(cacheKey)?.let { return it }
        return withContext(Dispatchers.IO) {
            runCatching {
                val info = findActivity(id) ?: return@runCatching null
                style.choices[id]?.let { chosenIcon(it, sizePx) }
                    ?: style.iconPack?.let { pack -> runCatching { packIcon(pack, info, sizePx) }.getOrNull() }
                    ?: systemIcon(info, sizePx, tint, background)
            }.onFailure { Log.w(LogTag, "icon failed for ${id.key}", it) }.getOrNull()?.also { iconCache.put(cacheKey, it) }
        }
    }

    override suspend fun iconLayers(id: AppId, sizePx: Int, tint: Int, background: Int?): IconLayers? {
        val style = iconStyle.value
        if (style.choices[id] != null || style.iconPack != null) return null
        val cacheKey = cacheKeyOf(id, sizePx, tint, background, style)
        layerCache.get(cacheKey)?.let { return it }
        return withContext(Dispatchers.IO) {
            runCatching {
                val info = findActivity(id) ?: return@runCatching null
                val drawable = info.getIcon(context.resources.displayMetrics.densityDpi)
                val (plate, glyph) = ThemedIconRenderer.renderLayers(drawable, tint, background, sizePx)
                IconLayers(plate, glyph)
            }.onFailure { Log.w(LogTag, "icon layers failed for ${id.key}", it) }.getOrNull()?.also { layerCache.put(cacheKey, it) }
        }
    }

    private fun chosenIcon(choice: AppIconChoice, sizePx: Int): Bitmap? =
        runCatching { iconPacks.named(choice.iconPack, choice.drawable)?.toBitmap(sizePx, sizePx) }.getOrNull()

    private fun packIcon(pack: String, info: LauncherActivityInfo, sizePx: Int): Bitmap? {
        iconPacks.icon(pack, info.componentName)?.let { return it.toBitmap(sizePx, sizePx) }
        val decoration = iconPacks.decoration(pack) ?: return null
        return IconPackRenderer.render(info.getIcon(0), decoration, info.componentName.packageName.hashCode(), sizePx)
    }

    private fun systemIcon(info: LauncherActivityInfo, sizePx: Int, tint: Int?, background: Int?): Bitmap {
        val drawable = info.getIcon(context.resources.displayMetrics.densityDpi)
        return if (tint != null) ThemedIconRenderer.render(drawable, tint, background, sizePx) else drawable.toBitmap(sizePx, sizePx)
    }

    private fun readApps(): List<LauncherApp> = runCatching {
        launcherApps.getActivityList(null, user)
            .filter { it.applicationInfo.packageName != context.packageName }
            .map { LauncherApp(AppId(it.componentName.packageName, it.componentName.className), it.label.toString()) }
            .sortedBy { it.label.lowercase() }
    }.onFailure { Log.w(LogTag, "reading apps failed", it) }.getOrDefault(emptyList())

    private fun findActivity(id: AppId): LauncherActivityInfo? =
        launcherApps.getActivityList(id.packageName, user).firstOrNull { it.componentName.className == id.activityName }

    private companion object {
        const val LogTag = "InstalledApps"
        const val PackageScheme = "package"
        const val IconCacheEntries = 256
    }
}
