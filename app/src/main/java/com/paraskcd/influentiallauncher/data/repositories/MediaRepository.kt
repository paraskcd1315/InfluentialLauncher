package com.paraskcd.influentiallauncher.data.repositories

import com.paraskcd.influentiallauncher.data.managers.NowPlayingManager
import com.paraskcd.influentiallauncher.data.types.MediaInfo
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val manager: NowPlayingManager
) {
    val info: StateFlow<MediaInfo?> get() = manager.info
    fun start() = manager.start()
    fun stop() = manager.stop()
    fun hasPermission(): Boolean = manager.hasNotificationAccess()
    fun playPause() = manager.playPause()
    fun next() = manager.next()
    fun previous() = manager.previous()
}