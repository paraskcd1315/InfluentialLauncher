// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellFailure
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellResult
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellState
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbConnectionManager
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbKeyStore
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbMdns
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.ResolvedService
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.ShellStarter
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.handback.HelperBinderSink
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.handback.HelperClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.conscrypt.Conscrypt
import java.security.Security

class ShellAccessImpl(
    private val context: Context,
    private val keyStore: AdbKeyStore,
    private val mdns: AdbMdns,
    private val starter: ShellStarter
) : ShellAccess {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _state = MutableStateFlow(initialState())
    override val state: StateFlow<ShellState> = _state.asStateFlow()

    private val helper = HelperClient(onDeath = {
        _state.value = ShellState.Starting
        ensureReady()
    })
    private var job: Job? = null

    init {
        HelperBinderSink.listen { binder -> helper.bind(binder) }
        runCatching {
            Security.insertProviderAt(Conscrypt.newProvider(), 1)
        }.onFailure { Log.w(LogTag, "conscrypt unavailable", it) }
    }

    private fun initialState(): ShellState =
        if (keyStore.paired) ShellState.Starting else ShellState.NotPaired

    override fun ensureReady() {
        if (job?.isActive == true) return
        job = scope.launch { bringUp() }
    }

    override fun startPairing(pairingCode: String) {
        if (job?.isActive == true) return
        job = scope.launch { pairThenBringUp(null, pairingCode) }
    }

    override fun pairAt(host: String, port: Int, pairingCode: String) {
        if (job?.isActive == true) return
        job = scope.launch { pairThenBringUp(ResolvedService(host, port), pairingCode) }
    }

    override fun retry() = ensureReady()

    override suspend fun run(command: String): ShellResult = withContext(Dispatchers.IO) {
        if (!helper.ready) ShellResult.Unavailable else helper.run(command)
    }

    override fun systemService(name: String): IBinder? = helper.systemService(name)

    private suspend fun pairThenBringUp(explicit: ResolvedService?, pairingCode: String) {
        _state.value = ShellState.Pairing
        val service = explicit ?: mdns.pairing(MdnsTimeoutMs)
        if (service == null) {
            Log.w(LogTag, "no pairing service found")
            _state.value = ShellState.Failed(ShellFailure.PairingFailed)
            return
        }
        Log.w(LogTag, "pairing to ${service.host}:${service.port}")
        val paired = runCatching {
            val connection = AdbConnectionManager(keyStore, Build.VERSION.SDK_INT)
            connection.pair(service.host, service.port, pairingCode)
        }.onFailure { Log.w(LogTag, "pairing failed", it) }.getOrDefault(false)
        if (!paired) {
            _state.value = ShellState.Failed(ShellFailure.PairingFailed)
            return
        }
        bringUp()
    }

    private suspend fun bringUp() {
        if (!keyStore.paired) {
            _state.value = ShellState.NotPaired
            return
        }
        if (helper.ready) {
            _state.value = ShellState.Ready
            return
        }
        if (!wifiConnected()) {
            _state.value = ShellState.WaitingForWifi
            return
        }
        _state.value = ShellState.Starting
        val turnedOn = enableWirelessDebugging()
        val connection = AdbConnectionManager(keyStore, Build.VERSION.SDK_INT)
        val connected = runCatching {
            val service = mdns.connect(MdnsTimeoutMs) ?: return@runCatching false
            Log.w(LogTag, "connecting to ${service.host}:${service.port}")
            connection.connect(service.host, service.port)
            connection.isConnected
        }.onFailure { Log.w(LogTag, "connect failed", it) }.getOrDefault(false)
        if (!connected) {
            _state.value = ShellState.Failed(ShellFailure.AdbUnreachable)
            if (turnedOn) disableWirelessDebugging()
            runCatching { connection.close() }
            return
        }
        runCatching { starter.start(connection) }
        val up = awaitHelper()
        runCatching { connection.close() }
        if (up) {
            grantPermissions()
            _state.value = ShellState.Ready
        } else {
            _state.value = ShellState.Failed(ShellFailure.HelperSilent)
        }
        if (turnedOn) disableWirelessDebugging()
    }

    private suspend fun awaitHelper(): Boolean {
        val deadline = System.currentTimeMillis() + HelperTimeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (helper.ready) return true
            delay(HelperPollMs)
        }
        return helper.ready
    }

    private fun grantPermissions() {
        if (ContextCompat.checkSelfPermission(context, SecureSettings) != PackageManager.PERMISSION_GRANTED) {
            helper.run("pm grant ${context.packageName} $SecureSettings")
        }
        if (!Settings.System.canWrite(context)) {
            helper.run("appops set ${context.packageName} WRITE_SETTINGS allow")
        }
    }

    private fun wifiConnected(): Boolean {
        val manager = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    private fun wirelessDebuggingOn(): Boolean =
        runCatching { Settings.Global.getInt(context.contentResolver, AdbWifiEnabled) }.getOrDefault(0) == 1

    private fun enableWirelessDebugging(): Boolean {
        if (wirelessDebuggingOn()) return false
        return runCatching {
            Settings.Global.putInt(context.contentResolver, AdbWifiEnabled, 1)
            true
        }.getOrDefault(false)
    }

    private fun disableWirelessDebugging() {
        if (helper.ready) {
            helper.run("settings put global $AdbWifiEnabled 0")
        } else {
            runCatching { Settings.Global.putInt(context.contentResolver, AdbWifiEnabled, 0) }
        }
    }

    private companion object {
        const val LogTag = "ShellAccess"
        const val LoopbackHost = "127.0.0.1"
        const val AdbWifiEnabled = "adb_wifi_enabled"
        const val SecureSettings = "android.permission.WRITE_SECURE_SETTINGS"
        const val MdnsTimeoutMs = 5_000L
        const val HelperTimeoutMs = 5_000L
        const val HelperPollMs = 150L
    }
}
