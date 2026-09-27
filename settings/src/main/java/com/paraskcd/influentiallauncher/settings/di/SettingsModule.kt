package com.paraskcd.influentiallauncher.settings.di

import com.paraskcd.influentiallauncher.settings.domain.ports.SettingsStore
import com.paraskcd.influentiallauncher.settings.infrastructure.DataStoreSettingsStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
    @Binds
    abstract fun bindSettingsStore(impl: DataStoreSettingsStore): SettingsStore
}
