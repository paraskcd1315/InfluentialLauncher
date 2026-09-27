package com.paraskcd.influentiallauncher.media.di

import com.paraskcd.influentiallauncher.media.domain.ports.MediaSource
import com.paraskcd.influentiallauncher.media.infrastructure.MediaSessionSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {
    @Binds
    abstract fun bindMediaSource(impl: MediaSessionSource): MediaSource
}
