// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.pairing

import android.app.NotificationManager
import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShellPairingReceiver : BroadcastReceiver() {
    @Inject
    lateinit var shell: ShellAccess

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ShellPairingService.ActionSubmit) return
        val code = RemoteInput.getResultsFromIntent(intent)
            ?.getCharSequence(ShellPairingService.KeyCode)?.toString()?.trim().orEmpty()
        val host = intent.getStringExtra(ShellPairingService.ExtraHost)
        val port = intent.getIntExtra(ShellPairingService.ExtraPort, 0)
        if (host != null && port > 0 && code.length >= PairingCodeLength) {
            shell.pairAt(host, port, code)
        }
        context.getSystemService(NotificationManager::class.java).cancel(ShellPairingService.NotifId)
        context.stopService(Intent(context, ShellPairingService::class.java))
    }

    private companion object {
        const val PairingCodeLength = 6
    }
}
