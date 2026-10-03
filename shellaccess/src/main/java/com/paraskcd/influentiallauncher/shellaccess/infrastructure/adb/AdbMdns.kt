// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

class AdbMdns(context: Context) {
    private val nsd = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    suspend fun pairing(timeoutMillis: Long): ResolvedService? = discover(PairingType, timeoutMillis)

    suspend fun connect(timeoutMillis: Long): ResolvedService? = discover(ConnectType, timeoutMillis)

    private suspend fun discover(serviceType: String, timeoutMillis: Long): ResolvedService? {
        val result = CompletableDeferred<ResolvedService?>()
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(type: String) = Unit
            override fun onDiscoveryStopped(type: String) = Unit
            override fun onStartDiscoveryFailed(type: String, code: Int) {
                if (!result.isCompleted) result.complete(null)
            }
            override fun onStopDiscoveryFailed(type: String, code: Int) = Unit
            override fun onServiceLost(info: NsdServiceInfo) = Unit
            override fun onServiceFound(info: NsdServiceInfo) {
                resolve(info, result)
            }
        }
        return try {
            nsd.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener)
            withTimeoutOrNull(timeoutMillis) { result.await() }
        } finally {
            runCatching { nsd.stopServiceDiscovery(listener) }
        }
    }

    private fun resolve(info: NsdServiceInfo, result: CompletableDeferred<ResolvedService?>) {
        nsd.resolveService(info, object : NsdManager.ResolveListener {
            override fun onResolveFailed(info: NsdServiceInfo, code: Int) {
                Log.w(LogTag, "resolve failed: $code")
            }
            override fun onServiceResolved(info: NsdServiceInfo) {
                val host = info.host?.hostAddress ?: return
                if (!result.isCompleted) result.complete(ResolvedService(host, info.port))
            }
        })
    }

    private companion object {
        const val LogTag = "AdbMdns"
        const val PairingType = "_adb-tls-pairing._tcp."
        const val ConnectType = "_adb-tls-connect._tcp."
    }
}
