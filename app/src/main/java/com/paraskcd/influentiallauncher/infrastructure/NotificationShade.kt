// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

object NotificationShade {
    private const val LogTag = "NotificationShade"
    private const val StatusBarService = "statusbar"
    private const val ExpandMethod = "expandNotificationsPanel"

    @SuppressLint("WrongConstant")
    fun expand(context: Context): Boolean = runCatching {
        val service = context.getSystemService(StatusBarService) ?: error("no status bar service")
        service.javaClass.getMethod(ExpandMethod).invoke(service)
    }.onFailure { Log.w(LogTag, "expanding the notification shade failed", it) }.isSuccess
}
