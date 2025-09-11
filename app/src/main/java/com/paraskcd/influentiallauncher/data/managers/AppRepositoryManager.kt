package com.paraskcd.influentiallauncher.data.managers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
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
import androidx.core.graphics.createBitmap
import kotlin.compareTo
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale

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

    fun getAppIcon(packageName: String, applyDynamicColoring: Boolean = false, dynamicColor: Int? = null): Drawable? {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            var icon = packageManager.getApplicationIcon(appInfo)

            if (applyDynamicColoring && dynamicColor != null) {
                val monochromeIcon = getMonochromeAppIcon(packageName)
                if (monochromeIcon != null) {
                    Log.d("ThemedIcons", "Using system monochrome icon for $packageName")
                    icon = createThemedIcon(monochromeIcon, dynamicColor)
                } else {
                    icon = applySubtleTheming(icon, dynamicColor)
                }
            }
            return icon
        } catch (e: Exception) {
            Log.e("AppRepositoryManager", "Error getting app icon for $packageName", e)
            return null
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getMonochromeAppIcon(packageName: String): Drawable? {
        try {
            val appInfo: ApplicationInfo = packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA
            )

            // Get the resources of the target application first
            val appResources: Resources = packageManager.getResourcesForApplication(appInfo)

            if (appInfo.icon != 0) {
                val iconDrawable: Drawable = appResources.getDrawable(appInfo.icon, null)

                if (iconDrawable is AdaptiveIconDrawable) {
                    return iconDrawable.monochrome
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppRepositoryManager", "Package not found for monochrome icon: $packageName", e)
        } catch (e: Resources.NotFoundException) {
            Log.e("AppRepositoryManager", "Icon resource not found for $packageName", e)
        } catch (e: Exception) {
            Log.e("AppRepositoryManager", "Error getting monochrome icon for $packageName", e)
        }
        return null
    }

    private fun createThemedIcon(monochromeIcon: Drawable, color: Int): Drawable {
        val size = 128
        val themedBitmap = createBitmap(size, size)
        val canvas = Canvas(themedBitmap)

        val backgroundPaint = Paint().apply {
            shader = getBgGradiant(color, size)
            isAntiAlias = true
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius * 0.9f, backgroundPaint)

        val iconSize = (size).toInt()
        val iconOffset = (size - iconSize) / 2f

        val tinted = monochromeIcon.mutate()
        tinted.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        val scaledIconBitmap = drawableToBitmap(tinted)
        val scaledBitmap = scaledIconBitmap.scale(iconSize, iconSize)

        canvas.drawBitmap(scaledBitmap, iconOffset, iconOffset, null)

        return themedBitmap.toDrawable(context.resources)
    }

    private fun applySubtleTheming(originalIcon: Drawable, themeColor: Int): Drawable {
        val size = 128
        val themedBitmap = createBitmap(size, size)
        val canvas = Canvas(themedBitmap)

        val backgroundPaint = Paint().apply {
            shader = getBgGradiant(themeColor, size)
            isAntiAlias = true
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius * 0.9f, backgroundPaint)

        val iconSize = (size * 0.65f).toInt()
        val iconOffset = (size - iconSize) / 2f

        val originalBitmap = drawableToBitmap(originalIcon)
        val scaledBitmap = originalBitmap.scale(iconSize, iconSize)

        val red = android.graphics.Color.red(themeColor) / 255f
        val green = android.graphics.Color.green(themeColor) / 255f
        val blue = android.graphics.Color.blue(themeColor) / 255f

        val photoshopColorizeMatrix = android.graphics.ColorMatrix().apply {
            val matrix = floatArrayOf(
                0.299f * red, 0.587f * red, 0.114f * red, 0f, 0f,
                0.299f * green, 0.587f * green, 0.114f * green, 0f, 0f,
                0.299f * blue, 0.587f * blue, 0.114f * blue, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
            set(matrix)
        }

        val colorizePaint = Paint().apply {
            colorFilter = android.graphics.ColorMatrixColorFilter(photoshopColorizeMatrix)
            isAntiAlias = true
        }

        canvas.drawBitmap(scaledBitmap, iconOffset, iconOffset, colorizePaint)

        return themedBitmap.toDrawable(context.resources)
    }

    private fun getBgGradiant(color: Int, size: Int): RadialGradient {
        return  RadialGradient(
                size / 2f, size / 2f, size / 2f,
        intArrayOf(
            color and 0x00FFFFFF or 0x30000000,
            color and 0x00FFFFFF or 0x60000000
        ),
        floatArrayOf(0f, 1f),
        Shader.TileMode.CLAMP
        )
    }

    private fun drawableToBitmap(drawable: Drawable): android.graphics.Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 128
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 128

        return createBitmap(width, height).also { bitmap ->
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }
}