// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeoutOrNull

class AdbMdns(context: Context) {
    private val nsd = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    suspend fun pairingPort(timeoutMillis: Long): Int? = discover(PairingType, timeoutMillis)

    suspend fun connectPort(timeoutMillis: Long): Int? = discover(ConnectType, timeoutMillis)

    private suspend fun discover(serviceType: String, timeoutMillis: Long): Int? {
        val port = CompletableDeferred<Int>()
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(type: String) = Unit
            override fun onDiscoveryStopped(type: String) = Unit
            override fun onStartDiscoveryFailed(type: String, code: Int) {
                port.complete(0)
            }
            override fun onStopDiscoveryFailed(type: String, code: Int) = Unit
            override fun onServiceLost(info: NsdServiceInfo) = Unit
            override fun onServiceFound(info: NsdServiceInfo) {
                resolve(info, port)
            }
        }
        return try {
            nsd.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
            val found = withTimeoutOrNull(timeoutMillis) { port.await() }
            found?.takeIf { it > 0 }
        } catch (error: TimeoutCancellationException) {
            null
        } finally {
            runCatching { nsd.stopServiceDiscovery(listener) }
        }
    }

    private fun resolve(info: NsdServiceInfo, port: CompletableDeferred<Int>) {
        nsd.resolveService(info, object : NsdManager.ResolveListener {
            override fun onResolveFailed(info: NsdServiceInfo, code: Int) {
                Log.w(LogTag, "resolve failed: $code")
            }
            override fun onServiceResolved(info: NsdServiceInfo) {
                if (!port.isCompleted) port.complete(info.port)
            }
        })
    }

    private companion object {
        const val LogTag = "AdbMdns"
        const val PairingType = "_adb-tls-pairing._tcp."
        const val ConnectType = "_adb-tls-connect._tcp."
    }
}
