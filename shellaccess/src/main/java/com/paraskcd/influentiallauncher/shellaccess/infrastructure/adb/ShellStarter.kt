// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.util.Log
import io.github.muntashirakon.adb.AbsAdbConnectionManager

class ShellStarter(private val context: Context) {
    fun start(connection: AbsAdbConnectionManager) {
        warmUp(connection)
        val apk = context.applicationInfo.sourceDir
        val packageName = context.packageName
        val uid = context.applicationInfo.uid
        val niceName = packageName.replace('.', '_') + NiceSuffix
        val serverClass = "com.paraskcd.influentiallauncher.shellaccess.infrastructure.server.InfShellServer"
        val command = buildString {
            append("touch /data/local/tmp/infl_ran; ")
            append("CLASSPATH=").append(apk).append(' ')
            append("nohup setsid app_process -Djava.class.path=").append(apk).append(' ')
            append("/system/bin --nice-name=").append(niceName).append(' ')
            append(serverClass).append(' ')
            append(packageName).append(' ')
            append(uid).append(' ')
            append(apk)
            append(" > /data/local/tmp/infl_helper.log 2>&1 < /dev/null &")
        }
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
        }.onFailure { Log.w(LogTag, "shell '$command' ended: ${it.message}") }
    }

    private companion object {
        const val LogTag = "ShellStarter"
        const val NiceSuffix = "_shell"
        const val WarmUpSettleMillis = 300L
    }
}
