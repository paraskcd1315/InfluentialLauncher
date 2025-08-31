package com.paraskcd.influentiallauncher.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemActionsService @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun openNotifications(): Boolean {
        return try {
            val sbservice = context.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val showsb = statusbarManager.getMethod("expandNotificationsPanel")
            showsb.invoke(sbservice)
            true
        } catch (e: Exception) {
            false
        }
    }
}