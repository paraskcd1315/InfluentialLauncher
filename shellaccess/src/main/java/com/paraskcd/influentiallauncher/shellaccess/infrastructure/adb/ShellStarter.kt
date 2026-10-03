// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.util.Log
import io.github.muntashirakon.adb.AbsAdbConnectionManager
import java.io.File

class ShellStarter(private val context: Context) {
    fun start(connection: AbsAdbConnectionManager) {
        warmUp(connection)
        val starter = File(context.applicationInfo.nativeLibraryDir, StarterLib).absolutePath
        val apk = context.applicationInfo.sourceDir
        val packageName = context.packageName
        val uid = context.applicationInfo.uid
        val command = "$starter --apk=$apk --pkg=$packageName --uid=$uid"
        runShell(connection, command)
    }

    private fun warmUp(connection: AbsAdbConnectionManager) {
        runShell(connection, "true")
        runCatching { Thread.sleep(WarmUpSettleMillis) }
    }

    private fun runShell(connection: AbsAdbConnectionManager, command: String) {
        runCatching {
            val stream = connection.openStream("shell:$command")
            runCatching { stream.openInputStream().use { it.readBytes() } }
            runCatching { stream.close() }
        }.onFailure { Log.w(LogTag, "shell ended: ${it.message}") }
    }

    private companion object {
        const val LogTag = "ShellStarter"
        const val StarterLib = "libinfluential.so"
        const val WarmUpSettleMillis = 300L
    }
}
