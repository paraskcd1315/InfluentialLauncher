package com.paraskcd.influentiallauncher.media.infrastructure

import android.app.ActivityOptions
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import com.paraskcd.influentiallauncher.media.domain.ports.MediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSessionSource @Inject constructor(
    @ApplicationContext private val context: Context
) : MediaSource {

    private val sessions = context.getSystemService(MediaSessionManager::class.java)
    private val listener = ComponentName(context, MediaListenerService::class.java)
    private var active: MediaController? = null

    override fun hasAccess(): Boolean = context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)

    override fun requestAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
            .putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, listener.flattenToString())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }.onFailure {
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun nowPlaying(): Flow<NowPlaying?> = callbackFlow {
        val handler = Handler(Looper.getMainLooper())
        val watched = mutableListOf<MediaController>()
        val callback = object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) { trySend(pick(watched)) }
            override fun onMetadataChanged(metadata: MediaMetadata?) { trySend(pick(watched)) }
            override fun onSessionDestroyed() { trySend(pick(watched)) }
        }
        fun watch(controllers: List<MediaController>?) {
            watched.forEach { it.unregisterCallback(callback) }
            watched.clear()
            controllers.orEmpty().forEach {
                it.registerCallback(callback, handler)
                watched.add(it)
            }
            trySend(pick(watched))
        }
        val sessionsChanged = MediaSessionManager.OnActiveSessionsChangedListener { watch(it) }
        val registered = runCatching {
            sessions.addOnActiveSessionsChangedListener(sessionsChanged, listener, handler)
            watch(sessions.getActiveSessions(listener))
        }.onFailure {
            Log.w(LogTag, "media sessions unavailable", it)
            trySend(null)
        }.isSuccess
        awaitClose {
            if (registered) sessions.removeOnActiveSessionsChangedListener(sessionsChanged)
            watched.forEach { it.unregisterCallback(callback) }
        }
    }.distinctUntilChanged()

    override fun playPause() {
        val controller = active ?: return
        val playing = controller.playbackState?.state == PlaybackState.STATE_PLAYING
        if (playing) controller.transportControls.pause() else controller.transportControls.play()
    }

    override fun next() {
        active?.transportControls?.skipToNext()
    }

    override fun previous() {
        active?.transportControls?.skipToPrevious()
    }

    override fun open() {
        val controller = active ?: return
        val options = ActivityOptions.makeBasic()
            .setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            .toBundle()
        val sent = controller.sessionActivity?.let { activity ->
            runCatching { activity.send(context, 0, null, null, null, null, options) }
                .onFailure { Log.w(LogTag, "session activity failed", it) }
                .isSuccess
        } ?: false
        if (sent) return
        context.packageManager.getLaunchIntentForPackage(controller.packageName)
            ?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ?.let { runCatching { context.startActivity(it) } }
    }

    private fun pick(controllers: List<MediaController>): NowPlaying? {
        val controller = controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: controllers.firstOrNull { it.metadata != null }
        active = controller
        val metadata = controller?.metadata ?: return null
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)?.takeIf { it.isNotBlank() } ?: return null
        return NowPlaying(
            title = title,
            artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST),
            artwork = artwork(metadata),
            playing = controller.playbackState?.state == PlaybackState.STATE_PLAYING,
            packageName = controller.packageName
        )
    }

    private fun artwork(metadata: MediaMetadata): Bitmap? =
        metadata.getBitmap(MediaMetadata.METADATA_KEY_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)

    private companion object {
        const val LogTag = "MediaSource"
    }
}
