// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.infrastructure

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log

object NotificationShade {
    private const val LogTag = "NotificationShade"
    private const val StatusBarService = "statusbar"
    private const val ExpandNotifications = "expandNotificationsPanel"
    private const val ExpandSettings = "expandSettingsPanel"

    fun expand(context: Context): Boolean = call(context, ExpandNotifications)

    fun expandSettings(context: Context): Boolean = call(context, ExpandSettings)

    @SuppressLint("WrongConstant")
    private fun call(context: Context, method: String): Boolean = runCatching {
        val service = context.getSystemService(StatusBarService) ?: error("no status bar service")
        service.javaClass.getMethod(method).invoke(service)
    }.onFailure { Log.w(LogTag, "$method failed", it) }.isSuccess
}
