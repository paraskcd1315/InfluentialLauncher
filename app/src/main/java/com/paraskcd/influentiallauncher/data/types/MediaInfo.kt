package com.paraskcd.influentiallauncher.data.types

import android.graphics.Bitmap

data class MediaInfo(
    val title: String?,
    val artist: String?,
    val album: String?,
    val isPlaying: Boolean,
    val artwork: Bitmap?,
    val packageName: String?
)
