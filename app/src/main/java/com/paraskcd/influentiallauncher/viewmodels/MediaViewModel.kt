package com.paraskcd.influentiallauncher.viewmodels

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paraskcd.influentiallauncher.data.repositories.MediaRepository
import com.paraskcd.influentiallauncher.data.types.MediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val repo: MediaRepository
): ViewModel() {
    private val _state = MutableStateFlow<MediaState>(MediaState.Loading)
    val state: StateFlow<MediaState> = _state

    init { refresh() }

    fun refresh() {
        if (!repo.hasPermission()) {
            _state.value = MediaState.PermissionRequired
            return
        }
        repo.start()
        viewModelScope.launch {
            repo.info.collectLatest { info ->
                _state.value = info?.let { MediaState.Ready(it) } ?: MediaState.Unavailable
            }
        }
    }

    fun openNotificationAccessSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun playPause() = repo.playPause()
    fun next() = repo.next()
    fun previous() = repo.previous()

    override fun onCleared() {
        super.onCleared()
        repo.stop()
    }
}