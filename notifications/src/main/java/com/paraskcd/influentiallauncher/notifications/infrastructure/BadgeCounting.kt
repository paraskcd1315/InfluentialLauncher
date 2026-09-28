// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.notifications.infrastructure

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Process
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

object BadgeCounting {
    fun countsOf(
        notifications: Array<StatusBarNotification>,
        ranking: NotificationListenerService.RankingMap,
        ownPackage: String
    ): Map<String, Int> {
        val me = Process.myUserHandle()
        val scratch = NotificationListenerService.Ranking()
        return notifications
            .filter { it.user == me && it.packageName != ownPackage && badges(it, ranking, scratch) }
            .groupBy { it.packageName }
            .mapValues { (_, posted) -> posted.sumOf { maxOf(1, it.notification.number) } }
    }

    private fun badges(
        posted: StatusBarNotification,
        ranking: NotificationListenerService.RankingMap,
        scratch: NotificationListenerService.Ranking
    ): Boolean {
        val notification = posted.notification
        val ranked = ranking.getRanking(posted.key, scratch)
        if (ranked && !scratch.canShowBadge() && !hiddenOnlyByDoNotDisturb(scratch)) return false
        val ongoing = notification.flags and Notification.FLAG_ONGOING_EVENT != 0
        if (ranked && ongoing && scratch.channel?.id == NotificationChannel.DEFAULT_CHANNEL_ID) return false
        if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return false
        val title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
        return !title.isNullOrEmpty() || !text.isNullOrEmpty()
    }

    private fun hiddenOnlyByDoNotDisturb(ranking: NotificationListenerService.Ranking): Boolean {
        val suppressed = ranking.suppressedVisualEffects and NotificationManager.Policy.SUPPRESSED_EFFECT_BADGE != 0
        return suppressed && ranking.channel?.canShowBadge() == true
    }
}
