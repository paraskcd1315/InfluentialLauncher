package com.paraskcd.influentiallauncher.devicestatus.di

import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import com.paraskcd.influentiallauncher.devicestatus.infrastructure.AndroidDeviceStatusSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceStatusModule {
    @Binds
    abstract fun bindDeviceStatusSource(impl: AndroidDeviceStatusSource): DeviceStatusSource
}
