package com.paraskcd.influentiallauncher.media.infrastructure

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.paraskcd.influentiallauncher.notifications.infrastructure.BadgeCounting
import com.paraskcd.influentiallauncher.notifications.infrastructure.ListenerNotificationBadges
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The notification listener: the identity media sessions need, and the source of icon badge counts. */
@AndroidEntryPoint
class MediaListenerService : NotificationListenerService() {
    @Inject
    lateinit var badges: ListenerNotificationBadges

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val changes = Channel<Unit>(Channel.CONFLATED)

    override fun onCreate() {
        super.onCreate()
        scope.launch {
            for (change in changes) recount()
        }
    }

    override fun onListenerConnected() = changed()

    override fun onListenerDisconnected() {
        badges.publish(emptyMap())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) = changed()

    override fun onNotificationRemoved(sbn: StatusBarNotification?, rankingMap: RankingMap?) = changed()

    override fun onNotificationRankingUpdate(rankingMap: RankingMap?) = changed()

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun changed() {
        changes.trySend(Unit)
    }

    private fun recount() {
        runCatching { BadgeCounting.countsOf(activeNotifications ?: emptyArray(), currentRanking, packageName) }
            .onSuccess(badges::publish)
            .onFailure { Log.w(LogTag, "badge count failed", it) }
    }

    private companion object {
        const val LogTag = "BadgeCounts"
    }
}
