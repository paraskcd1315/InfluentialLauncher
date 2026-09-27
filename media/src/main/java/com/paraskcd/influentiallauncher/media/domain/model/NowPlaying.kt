package com.paraskcd.influentiallauncher.media.domain.model

import android.graphics.Bitmap

data class NowPlaying(
    val title: String,
    val artist: String?,
    val artwork: Bitmap?,
    val playing: Boolean,
    val packageName: String
)
