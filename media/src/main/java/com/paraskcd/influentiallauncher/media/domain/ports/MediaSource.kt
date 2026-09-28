// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.media.domain.ports

import com.paraskcd.influentiallauncher.media.domain.model.NowPlaying
import kotlinx.coroutines.flow.Flow

interface MediaSource {
    fun hasAccess(): Boolean

    fun requestAccess()

    fun nowPlaying(): Flow<NowPlaying?>

    fun playPause()

    fun next()

    fun previous()

    fun open()
}
