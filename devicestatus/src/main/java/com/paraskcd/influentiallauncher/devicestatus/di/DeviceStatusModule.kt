// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.devicestatus.di

import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceStatusSource
import com.paraskcd.influentiallauncher.devicestatus.domain.ports.DeviceTiltSource
import com.paraskcd.influentiallauncher.devicestatus.infrastructure.AndroidDeviceStatusSource
import com.paraskcd.influentiallauncher.devicestatus.infrastructure.AndroidDeviceTiltSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceStatusModule {
    @Binds
    abstract fun bindDeviceStatusSource(impl: AndroidDeviceStatusSource): DeviceStatusSource

    @Binds
    abstract fun bindDeviceTiltSource(impl: AndroidDeviceTiltSource): DeviceTiltSource
}
