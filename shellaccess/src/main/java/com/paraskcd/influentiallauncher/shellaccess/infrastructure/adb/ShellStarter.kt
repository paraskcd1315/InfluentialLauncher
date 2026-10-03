// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb

import android.content.Context
import android.util.Log
import io.github.muntashirakon.adb.AbsAdbConnectionManager

class ShellStarter(private val context: Context) {
    fun start(connection: AbsAdbConnectionManager) {
        val apk = context.applicationInfo.sourceDir
        val packageName = context.packageName
        val uid = context.applicationInfo.uid
        val niceName = packageName.replace('.', '_') + NiceSuffix
        val serverClass = "com.paraskcd.influentiallauncher.shellaccess.infrastructure.server.InfShellServer"
        val command = buildString {
            append("CLASSPATH=").append(apk).append(' ')
            append("setsid app_process -Djava.class.path=").append(apk).append(' ')
            append("/system/bin --nice-name=").append(niceName).append(' ')
            append(serverClass).append(' ')
            append(packageName).append(' ')
            append(uid).append(' ')
            append(apk)
            append(" > /dev/null 2>&1 < /dev/null &")
        }
        val stream = connection.openStream("shell:$command")
        runCatching {
            stream.openInputStream().use { it.readBytes() }
        }.onFailure { Log.w(LogTag, "starter stream ended: ${it.message}") }
        runCatching { stream.close() }
    }

    private companion object {
        const val LogTag = "ShellStarter"
        const val NiceSuffix = "_shell"
    }
}
