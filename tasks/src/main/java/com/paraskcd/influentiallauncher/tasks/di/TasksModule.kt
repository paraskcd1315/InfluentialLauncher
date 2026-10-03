// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.tasks.di

import com.paraskcd.influentiallauncher.tasks.domain.ports.AppActions
import com.paraskcd.influentiallauncher.tasks.domain.ports.AppDataUsage
import com.paraskcd.influentiallauncher.tasks.domain.ports.OpenApps
import com.paraskcd.influentiallauncher.tasks.infrastructure.HelperOpenApps
import com.paraskcd.influentiallauncher.tasks.infrastructure.NetworkStatsAppDataUsage
import com.paraskcd.influentiallauncher.tasks.infrastructure.ShellAppActions
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksModule {
    @Binds
    abstract fun bindOpenApps(impl: HelperOpenApps): OpenApps

    @Binds
    abstract fun bindAppActions(impl: ShellAppActions): AppActions

    @Binds
    abstract fun bindAppDataUsage(impl: NetworkStatsAppDataUsage): AppDataUsage
}
