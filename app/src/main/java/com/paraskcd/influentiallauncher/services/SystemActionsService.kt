package com.paraskcd.influentiallauncher.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class SystemActionsService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        return super.onUnbind(intent)
    }

    override fun onInterrupt() { /* no-op */ }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* no-op */ }

    companion object {
        @Volatile private var instance: SystemActionsService? = null

        fun isEnabled(): Boolean = instance != null

        fun openNotifications(): Boolean {
            val ok = instance?.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS) ?: false
            return ok
        }

        fun openQuickSettings(): Boolean {
            val ok = instance?.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS) ?: false
            return ok
        }
    }
}