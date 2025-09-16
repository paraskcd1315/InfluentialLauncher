package com.paraskcd.influentiallauncher.data.types

sealed class MediaState {
    object Loading : MediaState()
    object PermissionRequired : MediaState()
    object Unavailable : MediaState()
    data class Ready(val info: MediaInfo) : MediaState()
}