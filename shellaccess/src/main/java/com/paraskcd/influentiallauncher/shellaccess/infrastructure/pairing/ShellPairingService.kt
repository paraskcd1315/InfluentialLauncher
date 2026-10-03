// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.pairing

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.drawable.Icon
import android.os.IBinder
import com.paraskcd.influentiallauncher.shellaccess.R
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbMdns
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.ResolvedService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShellPairingService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(Channel, ChannelName, NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NotifId, searchingNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        if (job?.isActive != true) {
            job = scope.launch {
                val service = discover()
                if (service == null) {
                    stopSelf()
                } else {
                    getSystemService(NotificationManager::class.java).notify(NotifId, codeNotification(service))
                }
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun discover(): ResolvedService? {
        val mdns = AdbMdns(this)
        val deadline = System.currentTimeMillis() + DiscoverWindowMs
        while (System.currentTimeMillis() < deadline) {
            val found = mdns.pairing(DiscoverStepMs)
            if (found != null) return found
        }
        return null
    }

    private fun searchingNotification(): Notification =
        Notification.Builder(this, Channel)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(getString(R.string.shellaccess_pair_searching_title))
            .setContentText(getString(R.string.shellaccess_pair_searching_text))
            .setOngoing(true)
            .build()

    private fun codeNotification(service: ResolvedService): Notification {
        val remoteInput = RemoteInput.Builder(KeyCode)
            .setLabel(getString(R.string.shellaccess_pair_code_label))
            .build()
        val submit = Intent(this, ShellPairingReceiver::class.java).apply {
            action = ActionSubmit
            putExtra(ExtraHost, service.host)
            putExtra(ExtraPort, service.port)
        }
        val pending = PendingIntent.getBroadcast(
            this, 0, submit,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val action = Notification.Action.Builder(
            Icon.createWithResource(this, android.R.drawable.ic_menu_send),
            getString(R.string.shellaccess_pair_action),
            pending
        ).addRemoteInput(remoteInput).build()
        return Notification.Builder(this, Channel)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(getString(R.string.shellaccess_pair_code_title))
            .setContentText(getString(R.string.shellaccess_pair_code_text))
            .setOngoing(true)
            .addAction(action)
            .build()
    }

    override fun onDestroy() {
        job?.cancel()
        getSystemService(NotificationManager::class.java).cancel(NotifId)
        super.onDestroy()
    }

    companion object {
        const val Channel = "shellaccess_pair"
        const val ChannelName = "Shell access pairing"
        const val NotifId = 48110
        const val ActionSubmit = "com.paraskcd.influentiallauncher.shellaccess.PAIR_SUBMIT"
        const val KeyCode = "code"
        const val ExtraHost = "host"
        const val ExtraPort = "port"
        private const val DiscoverWindowMs = 120_000L
        private const val DiscoverStepMs = 8_000L
    }
}
