// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.infrastructure.transport

import android.os.IBinder
import android.util.Log

object SystemServices {
    fun binder(name: String): IBinder? = runCatching {
        Class.forName("android.os.ServiceManager")
            .getMethod("getService", String::class.java)
            .invoke(null, name) as? IBinder
    }.onFailure { Log.w(LogTag, "cannot reach service $name", it) }.getOrNull()

    private const val LogTag = "SystemServices"
}
