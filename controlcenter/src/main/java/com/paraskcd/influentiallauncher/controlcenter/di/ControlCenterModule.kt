// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.controlcenter.di

import com.paraskcd.influentiallauncher.controlcenter.domain.ports.SystemControls
import com.paraskcd.influentiallauncher.controlcenter.infrastructure.AndroidSystemControls
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ControlCenterModule {
    @Binds
    abstract fun bindSystemControls(impl: AndroidSystemControls): SystemControls
}
