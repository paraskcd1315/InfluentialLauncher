// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShellProofReceiver : BroadcastReceiver() {
    @Inject
    lateinit var shell: ShellAccess

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        scope.launch {
            shell.state.collect { Log.i(LogTag, "state=$it") }
        }
        when (intent.action) {
            ActionPair -> {
                val code = intent.getStringExtra(ExtraCode).orEmpty()
                Log.i(LogTag, "pair with code of length ${code.length}")
                shell.startPairing(code)
            }
            ActionReady -> {
                Log.i(LogTag, "ensureReady")
                shell.ensureReady()
            }
            ActionRun -> {
                val command = intent.getStringExtra(ExtraCommand).orEmpty()
                scope.launch {
                    val result = shell.run(command)
                    Log.i(LogTag, "run '$command' -> code=${result.code} out=${result.out.trim()} err=${result.err.trim()}")
                }
            }
            ActionPairDirect -> {
                val code = intent.getStringExtra(ExtraCode).orEmpty()
                val host = intent.getStringExtra(ExtraHost).orEmpty()
                val port = intent.getIntExtra(ExtraPort, 0)
                Log.i(LogTag, "pairDirect $host:$port codeLen=${code.length}")
                shell.pairAt(host, port, code)
            }
        }
    }

    private companion object {
        const val LogTag = "ShellProof"
        const val ActionPair = "infl.shell.PAIR"
        const val ActionPairDirect = "infl.shell.PAIRDIRECT"
        const val ActionReady = "infl.shell.READY"
        const val ActionRun = "infl.shell.RUN"
        const val ExtraCode = "code"
        const val ExtraHost = "host"
        const val ExtraPort = "port"
        const val ExtraCommand = "cmd"
    }
}
