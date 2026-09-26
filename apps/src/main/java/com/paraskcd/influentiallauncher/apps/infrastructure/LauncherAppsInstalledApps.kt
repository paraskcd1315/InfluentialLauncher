package com.paraskcd.influentiallauncher.apps.infrastructure

import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Process
import android.os.UserHandle
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.paraskcd.influentiallauncher.apps.domain.model.AppId
import com.paraskcd.influentiallauncher.apps.domain.model.LauncherApp
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LauncherAppsInstalledApps @Inject constructor(
    @ApplicationContext private val context: Context
) : InstalledApps {

    private val launcherApps = context.getSystemService(LauncherApps::class.java)
    private val user: UserHandle = Process.myUserHandle()

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

    override fun launch(id: AppId, sourceBounds: Rect?): Boolean = runCatching {
        launcherApps.startMainActivity(ComponentName(id.packageName, id.activityName), user, sourceBounds, null)
    }.onFailure { Log.w(LogTag, "launch failed for ${id.key}", it) }.isSuccess

    override suspend fun icon(id: AppId, sizePx: Int): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val info = findActivity(id) ?: return@runCatching null
            info.getIcon(context.resources.displayMetrics.densityDpi).toBitmap(sizePx, sizePx)
        }.onFailure { Log.w(LogTag, "icon failed for ${id.key}", it) }.getOrNull()
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
    }
}
