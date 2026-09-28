// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.media.domain.model

import android.graphics.Bitmap

data class NowPlaying(
    val title: String,
    val artist: String?,
    val artwork: Bitmap?,
    val playing: Boolean,
    val packageName: String
)
