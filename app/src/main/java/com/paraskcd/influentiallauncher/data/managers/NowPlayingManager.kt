package com.paraskcd.influentiallauncher.data.managers

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.provider.Settings
import com.paraskcd.influentiallauncher.data.types.MediaInfo
import com.paraskcd.influentiallauncher.services.ActiveMediaListenerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NowPlayingManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val listeningComponent = ComponentName(context, ActiveMediaListenerService::class.java)
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    private var controllers: List<MediaController> = emptyList()
    private var currentController: MediaController? = null
    private val controllerCallbacks = mutableMapOf<MediaController, MediaController.Callback>()
    private var activeListener: MediaSessionManager.OnActiveSessionsChangedListener? = null

    private val _info = MutableStateFlow<MediaInfo?>(null)
    val info = _info.asStateFlow()

    fun start() {
        if (!hasNotificationAccess()) {
            _info.value = null
            return
        }
        controllers = runCatching {
            msm.getActiveSessions(listeningComponent)
        }.getOrDefault(emptyList())
        chooseController()
        if (activeListener == null) {
            activeListener = MediaSessionManager.OnActiveSessionsChangedListener { updated ->
                controllers = updated ?: emptyList()
                chooseController()
            }
            msm.addOnActiveSessionsChangedListener(activeListener!!, listeningComponent)
        }
    }

    fun stop() {
        activeListener?.let { msm.removeOnActiveSessionsChangedListener(it) }
        activeListener = null
        clearCallbacks()
        currentController = null
        _info.value = null
    }

    fun hasNotificationAccess(): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
        return enabled?.contains(context.packageName) == true
    }

    private fun chooseController() {
        val playing = controllers.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
        val chosen = playing ?: controllers.firstOrNull()
        if (chosen != currentController) swapController(chosen)
        updateInfoFrom(chosen)
    }

    private fun swapController(newController: MediaController?) {
        clearCallbacks()
        currentController = newController
        newController?.let { ctrl ->
            val cb = object : MediaController.Callback() {
                override fun onPlaybackStateChanged(state: PlaybackState?) { updateInfoFrom(ctrl) }
                override fun onMetadataChanged(metadata: MediaMetadata?) { updateInfoFrom(ctrl) }
                override fun onSessionDestroyed() {
                    scope.launch {
                        controllers = controllers.filter { it != ctrl }
                        chooseController()
                    }
                }
            }
            controllerCallbacks[ctrl] = cb
            ctrl.registerCallback(cb)
        }
    }

    private fun clearCallbacks() {
        controllerCallbacks.forEach { (c, cb) -> runCatching { c.unregisterCallback(cb) } }
        controllerCallbacks.clear()
    }

    private fun updateInfoFrom(ctrl: MediaController?) {
        scope.launch {
            if (ctrl == null) { _info.value = null; return@launch }
            val md = ctrl.metadata
            val st = ctrl.playbackState
            val art = md?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: md?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: md?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)

            _info.value = MediaInfo(
                title = md?.getString(MediaMetadata.METADATA_KEY_TITLE),
                artist = md?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                    ?: md?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST),
                album = md?.getString(MediaMetadata.METADATA_KEY_ALBUM),
                isPlaying = st?.state == PlaybackState.STATE_PLAYING,
                artwork = art,
                packageName = ctrl.packageName
            )
        }
    }

    fun playPause() {
        val s = currentController?.playbackState?.state
        val t = currentController?.transportControls ?: return
        if (s == PlaybackState.STATE_PLAYING) t.pause() else t.play()
    }
    fun next() { currentController?.transportControls?.skipToNext() }
    fun previous() { currentController?.transportControls?.skipToPrevious() }
}