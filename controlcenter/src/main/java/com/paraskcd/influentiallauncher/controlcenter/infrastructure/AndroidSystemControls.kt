// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.infrastructure

import android.app.NotificationManager
import android.app.UiModeManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.controlcenter.domain.model.ControlState
import com.paraskcd.influentiallauncher.controlcenter.domain.model.QuickToggle
import com.paraskcd.influentiallauncher.controlcenter.domain.ports.SystemControls
import com.paraskcd.influentiallauncher.controlcenter.infrastructure.shell.ShellCommands
import com.paraskcd.influentiallauncher.controlcenter.infrastructure.shell.ShellCommands.Values
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToInt

@Singleton
class AndroidSystemControls @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shell: ShellAccess
) : SystemControls {

    private val resolver = context.contentResolver
    private val wifi = context.getSystemService(WifiManager::class.java)
    private val bluetooth = context.getSystemService(BluetoothManager::class.java)?.adapter
    private val telephony = context.getSystemService(TelephonyManager::class.java)
    private val power = context.getSystemService(PowerManager::class.java)
    private val notifications = context.getSystemService(NotificationManager::class.java)
    private val uiMode = context.getSystemService(UiModeManager::class.java)
    private val location = context.getSystemService(LocationManager::class.java)
    private val audio = context.getSystemService(AudioManager::class.java)
    private val camera = context.getSystemService(CameraManager::class.java)
    private val torchCamera: String? by lazy {
        runCatching {
            camera.cameraIdList.firstOrNull { id ->
                camera.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrNull()
    }
    private val torch = MutableStateFlow(false)

    private val changes: Flow<Unit> = callbackFlow {
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        listOf(Settings.Global.CONTENT_URI, Settings.Secure.CONTENT_URI, Settings.System.CONTENT_URI).forEach {
            resolver.registerContentObserver(it, true, observer)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(Unit)
            }
        }
        val filter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            addAction(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
            addAction(LocationManager.MODE_CHANGED_ACTION)
            addAction(Intent.ACTION_CONFIGURATION_CHANGED)
            addAction(VolumeChangedAction)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        val torchCallback = object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                if (cameraId == torchCamera) {
                    torch.value = enabled
                    trySend(Unit)
                }
            }
        }
        camera.registerTorchCallback(torchCallback, handler)
        trySend(Unit)
        awaitClose {
            resolver.unregisterContentObserver(observer)
            context.unregisterReceiver(receiver)
            camera.unregisterTorchCallback(torchCallback)
        }
    }.conflate()

    override val state: Flow<ControlState> = combine(changes, torch, shell.state) { _, torchOn, shellState ->
        ControlState(
            toggles = QuickToggle.entries.associateWith { read(it, torchOn) },
            brightness = brightnessPosition(),
            autoBrightness = systemInt(Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC,
            volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() /
                audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1),
            ready = shellState is ShellState.Ready
        )
    }.distinctUntilChanged()

    override suspend fun set(toggle: QuickToggle, on: Boolean) {
        val command = when (toggle) {
            QuickToggle.Wifi -> ShellCommands.Wifi.format(if (on) Values.Enabled else Values.Disabled)
            QuickToggle.Bluetooth -> ShellCommands.Bluetooth.format(if (on) Values.Enable else Values.Disable)
            QuickToggle.Airplane -> ShellCommands.Airplane.format(if (on) Values.Enable else Values.Disable)
            QuickToggle.MobileData -> ShellCommands.MobileData.format(if (on) Values.Enable else Values.Disable)
            QuickToggle.DoNotDisturb -> ShellCommands.DoNotDisturb.format(if (on) Values.On else Values.Off)
            QuickToggle.BatterySaver -> ShellCommands.BatterySaver.format(if (on) Values.One else Values.Zero)
            QuickToggle.NightLight -> ShellCommands.NightLight.format(if (on) Values.One else Values.Zero)
            QuickToggle.DarkMode -> ShellCommands.DarkMode.format(if (on) Values.Yes else Values.No)
            QuickToggle.RotationLock -> ShellCommands.AutoRotate.format(if (on) Values.Zero else Values.One)
            QuickToggle.Location -> ShellCommands.Location.format(if (on) Values.True else Values.False)
            QuickToggle.Flashlight -> {
                val id = torchCamera ?: return
                runCatching { camera.setTorchMode(id, on) }.onFailure { Log.w(LogTag, "torch failed", it) }
                return
            }
        }
        if (!shell.run(command).ok) Log.w(LogTag, "$toggle did not change")
    }

    override fun setBrightness(level: Float) {
        if (!Settings.System.canWrite(context)) {
            shell.ensureReady()
            return
        }
        runCatching {
            if (systemInt(Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
            }
            val value = (level.coerceIn(0f, 1f).pow(BrightnessGamma) * MaxBrightness).roundToInt().coerceIn(MinBrightness, MaxBrightness)
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, value)
        }.onFailure { Log.w(LogTag, "brightness failed", it) }
    }

    override fun setAutoBrightness(on: Boolean) {
        runCatching {
            Settings.System.putInt(
                resolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                if (on) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
        }.onFailure { Log.w(LogTag, "auto brightness failed", it) }
    }

    override fun setVolume(level: Float) {
        val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        runCatching { audio.setStreamVolume(AudioManager.STREAM_MUSIC, (level.coerceIn(0f, 1f) * max).roundToInt(), 0) }
            .onFailure { Log.w(LogTag, "volume failed", it) }
    }

    override fun requestAccess() = shell.ensureReady()

    override fun openDetails(toggle: QuickToggle) {
        val action = when (toggle) {
            QuickToggle.Wifi -> Settings.Panel.ACTION_WIFI
            QuickToggle.Bluetooth -> Settings.ACTION_BLUETOOTH_SETTINGS
            QuickToggle.Airplane -> Settings.ACTION_AIRPLANE_MODE_SETTINGS
            QuickToggle.MobileData -> Settings.ACTION_NETWORK_OPERATOR_SETTINGS
            QuickToggle.Flashlight -> return
            QuickToggle.DoNotDisturb -> ZenModeSettings
            QuickToggle.BatterySaver -> Settings.ACTION_BATTERY_SAVER_SETTINGS
            QuickToggle.NightLight -> Settings.ACTION_NIGHT_DISPLAY_SETTINGS
            QuickToggle.DarkMode -> DarkThemeSettings
            QuickToggle.RotationLock -> Settings.ACTION_DISPLAY_SETTINGS
            QuickToggle.Location -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
        }
        open(Intent(action))
    }

    override fun openVolumePanel() = open(Intent(Settings.Panel.ACTION_VOLUME))

    override fun openSettings() = open(Intent(Settings.ACTION_SETTINGS))

    private fun read(toggle: QuickToggle, torchOn: Boolean): Boolean = runCatching {
        when (toggle) {
            QuickToggle.Wifi -> wifi.isWifiEnabled
            QuickToggle.Bluetooth -> bluetooth?.isEnabled == true
            QuickToggle.Airplane -> globalInt(Settings.Global.AIRPLANE_MODE_ON) == 1
            QuickToggle.MobileData -> telephony.isDataEnabled
            QuickToggle.Flashlight -> torchOn
            QuickToggle.DoNotDisturb -> notifications.currentInterruptionFilter > NotificationManager.INTERRUPTION_FILTER_ALL
            QuickToggle.BatterySaver -> power.isPowerSaveMode
            QuickToggle.NightLight -> Settings.Secure.getInt(resolver, NightDisplayActivated, 0) == 1
            QuickToggle.DarkMode -> uiMode.nightMode == UiModeManager.MODE_NIGHT_YES
            QuickToggle.RotationLock -> systemInt(Settings.System.ACCELEROMETER_ROTATION) == 0
            QuickToggle.Location -> location.isLocationEnabled
        }
    }.getOrDefault(false)

    private fun brightnessPosition(): Float {
        val value = systemInt(Settings.System.SCREEN_BRIGHTNESS).coerceIn(0, MaxBrightness)
        return (value.toFloat() / MaxBrightness).pow(1f / BrightnessGamma)
    }

    private fun systemInt(name: String): Int = runCatching { Settings.System.getInt(resolver, name) }.getOrDefault(0)

    private fun globalInt(name: String): Int = runCatching { Settings.Global.getInt(resolver, name) }.getOrDefault(0)

    private fun open(intent: Intent) {
        runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .onFailure { Log.w(LogTag, "cannot open ${intent.action}", it) }
    }

    private companion object {
        const val LogTag = "SystemControls"
        const val VolumeChangedAction = "android.media.VOLUME_CHANGED_ACTION"
        const val NightDisplayActivated = "night_display_activated"
        const val ZenModeSettings = "android.settings.ZEN_MODE_SETTINGS"
        const val DarkThemeSettings = "android.settings.DARK_THEME_SETTINGS"
        const val BrightnessGamma = 2.2f
        const val MaxBrightness = 255
        const val MinBrightness = 1
    }
}
