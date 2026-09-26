package com.paraskcd.influentiallauncher.pins.di

import com.paraskcd.influentiallauncher.pins.domain.ports.PinStore
import com.paraskcd.influentiallauncher.pins.infrastructure.DataStorePinStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PinsModule {
    @Binds
    abstract fun bindPinStore(impl: DataStorePinStore): PinStore
}
