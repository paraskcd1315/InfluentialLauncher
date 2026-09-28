// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.clock.di

import com.paraskcd.influentiallauncher.clock.domain.ports.WallClock
import com.paraskcd.influentiallauncher.clock.infrastructure.BroadcastWallClock
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ClockModule {
    @Binds
    abstract fun bindWallClock(impl: BroadcastWallClock): WallClock
}
