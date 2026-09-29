// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure.spotlight

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log

class SpotlightPeek(private val context: Context, private val onClosed: () -> Unit) {

    private var service: Messenger? = null
    private var boundPackage: String? = null
    private var peeking = false

    private val replies = Messenger(Handler(Looper.getMainLooper()) { message ->
        if (message.what == SpotlightPeekProtocol.Closed && fromSpotlight(message)) close()
        true
    })

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            service = Messenger(binder)
            send(SpotlightPeekProtocol.Register)
        }

        override fun onServiceDisconnected(name: ComponentName) = lost()

        override fun onBindingDied(name: ComponentName) {
            unbind()
            bind()
        }

        override fun onNullBinding(name: ComponentName) = lost()
    }

    val ready: Boolean get() = service != null

    fun bind() {
        if (boundPackage != null) return
        for (pkg in SpotlightPeekProtocol.Packages) {
            val intent = Intent(SpotlightPeekProtocol.ActionPeek).setPackage(pkg)
            val bound = runCatching { context.bindService(intent, connection, Context.BIND_AUTO_CREATE) }
                .onFailure { Log.w(LogTag, "binding $pkg failed", it) }
                .getOrDefault(false)
            if (bound) {
                boundPackage = pkg
                return
            }
            runCatching { context.unbindService(connection) }
        }
    }

    fun unbind() {
        if (boundPackage == null) return
        runCatching { context.unbindService(connection) }
        boundPackage = null
        lost()
    }

    fun start(): Boolean {
        val pkg = boundPackage ?: return false
        if (service == null) return false
        val intent = context.packageManager.getLaunchIntentForPackage(pkg) ?: return false
        val options = ActivityOptions.makeCustomAnimation(context, 0, 0)
        peeking = runCatching {
            context.startActivity(
                intent.putExtra(SpotlightPeekProtocol.ExtraPeek, true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                options.toBundle()
            )
        }.onFailure { Log.w(LogTag, "starting Spotlight peek failed", it) }.isSuccess
        return peeking
    }

    fun progress(value: Float) {
        if (!peeking) return
        send(SpotlightPeekProtocol.Progress, SpotlightPeekProtocol.KeyProgress, value.coerceIn(0f, 1f))
    }

    fun commit(velocityUp: Float) {
        if (!peeking) return
        send(SpotlightPeekProtocol.Commit, SpotlightPeekProtocol.KeyVelocity, velocityUp)
    }

    fun cancel(velocityUp: Float) {
        if (!peeking) return
        send(SpotlightPeekProtocol.Cancel, SpotlightPeekProtocol.KeyVelocity, velocityUp)
    }

    private fun send(what: Int, key: String? = null, value: Float = 0f) {
        val target = service ?: return
        val message = Message.obtain(null, what).apply {
            replyTo = replies
            if (key != null) data = Bundle().apply { putFloat(key, value) }
        }
        try {
            target.send(message)
        } catch (error: RemoteException) {
            Log.w(LogTag, "Spotlight peek message $what failed", error)
            lost()
        }
    }

    private fun fromSpotlight(message: Message): Boolean {
        val pkg = boundPackage ?: return false
        val uid = runCatching { context.packageManager.getPackageUid(pkg, 0) }.getOrNull() ?: return false
        return message.sendingUid == uid
    }

    private fun lost() {
        service = null
        close()
    }

    private fun close() {
        if (!peeking) return
        peeking = false
        onClosed()
    }

    private companion object {
        const val LogTag = "SpotlightPeek"
    }
}
