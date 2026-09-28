// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.di

import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import com.paraskcd.influentiallauncher.tasks.infrastructure.ShizukuOpenApps
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksModule {
    @Binds
    abstract fun bindOpenApps(impl: ShizukuOpenApps): OpenApps
}
