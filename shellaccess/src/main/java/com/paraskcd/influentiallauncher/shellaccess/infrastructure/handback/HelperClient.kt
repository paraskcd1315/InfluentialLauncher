// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.handback

import android.os.IBinder
import android.util.Log
import com.paraskcd.influentiallauncher.shellaccess.IInfShell
import com.paraskcd.influentiallauncher.shellaccess.domain.model.ShellResult
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.ShellBinderWrapper
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.ShellProtocol
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport.SystemServices

class HelperClient(private val onDeath: () -> Unit) {
    @Volatile
    private var shell: IInfShell? = null

    val ready: Boolean
        get() = runCatching { shell?.asBinder()?.pingBinder() == true }.getOrDefault(false)

    fun bind(binder: IBinder) {
        val remote = IInfShell.Stub.asInterface(binder)
        runCatching {
            binder.linkToDeath({
                shell = null
                onDeath()
            }, 0)
        }
        shell = remote
    }

    fun version(): Int? = runCatching { shell?.version() }.getOrNull()

    fun run(command: String): ShellResult {
        val remote = shell ?: return ShellResult.Unavailable
        return runCatching {
            val bundle = remote.run(command)
            ShellResult(
                code = bundle.getInt(ShellProtocol.ResultCode, -1),
                out = bundle.getString(ShellProtocol.ResultOut).orEmpty(),
                err = bundle.getString(ShellProtocol.ResultErr).orEmpty()
            )
        }.onFailure { Log.w(LogTag, "run failed", it) }.getOrDefault(ShellResult.Unavailable)
    }

    fun systemService(name: String): IBinder? {
        val remote = shell ?: return null
        val original = SystemServices.binder(name) ?: return null
        return ShellBinderWrapper(original, remote.asBinder())
    }

    fun stop() {
        runCatching { shell?.exit() }
        shell = null
    }

    private companion object {
        const val LogTag = "HelperClient"
    }
}
