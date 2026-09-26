package com.paraskcd.influentiallauncher.taskbar.di

import com.paraskcd.influentiallauncher.taskbar.domain.ports.PinStore
import com.paraskcd.influentiallauncher.taskbar.infrastructure.DataStorePinStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TaskbarModule {
    @Binds
    abstract fun bindPinStore(impl: DataStorePinStore): PinStore
}
