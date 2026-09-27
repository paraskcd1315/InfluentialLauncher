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
