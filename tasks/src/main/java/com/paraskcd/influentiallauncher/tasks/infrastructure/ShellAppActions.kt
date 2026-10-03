// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.infrastructure

import android.util.Log
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppActions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShellAppActions @Inject constructor(
    private val shell: ShellAccess
) : AppActions {
    override suspend fun forceStop(packageName: String) {
        if (!shell.run("am force-stop $packageName").ok) Log.w(LogTag, "force-stop $packageName failed")
    }

    override suspend fun clearStorage(packageName: String) {
        if (!shell.run("pm clear $packageName").ok) Log.w(LogTag, "clear $packageName failed")
    }

    private companion object {
        const val LogTag = "AppActions"
    }
}
