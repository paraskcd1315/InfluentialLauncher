package com.paraskcd.influentiallauncher.data.managers

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.paraskcd.influentiallauncher.data.types.AppEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _apps = MutableStateFlow(loadInstalledApps())
    val apps: StateFlow<List<AppEntry>> = _apps.asStateFlow()
    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            _apps.value = loadInstalledApps()
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        context.registerReceiver(packageReceiver, filter)
    }

    private fun loadInstalledApps(): List<AppEntry> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        return activities.mapNotNull { info ->
            val appInfo = info.activityInfo
            val packageName = appInfo.packageName
            val activityName = appInfo.name
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                val label = appInfo.loadLabel(packageManager).toString()
                val icon = appInfo.loadIcon(packageManager)

                AppEntry(
                    label = label,
                    packageName = packageName,
                    activityName = activityName,
                    icon = icon,
                    onClick = {
                        val explicit = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            setClassName(packageName, activityName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        }
                        try {
                            context.startActivity(explicit)
                        } catch (e: Exception) {
                            Log.e("AppRepositoryManager", "Error launching app $packageName", e)
                            Toast.makeText(context, "Could not open app", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            else null
        }
        .sortedBy {
            it.label.lowercase()
        }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(appInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    fun launchApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent?.component == null) {
            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
            return
        }

        val activityInfo = packageManager.getActivityInfo(launchIntent.component!!, 0)

        val explicit = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setClassName(packageName, activityInfo.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }

        try {
            context.startActivity(explicit)
        } catch (e: Exception) {
            Log.e("AppRepositoryManager", "Error launching app $packageName", e)
            Toast.makeText(context, "Could not open app", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppInfo(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AppRepositoryManager", "Error launching app info $packageName", e)
            Toast.makeText(context, "Opening App info not available", Toast.LENGTH_SHORT).show()
        }
    }

    fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = "package:${packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AppRepositoryManager", "Error launching app info $packageName", e)
            Toast.makeText(context, "Uninstall not available", Toast.LENGTH_SHORT).show()
        }
    }
}