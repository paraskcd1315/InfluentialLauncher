// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.apps.di

import com.paraskcd.influentiallauncher.apps.domain.ports.IconPacks
import com.paraskcd.influentiallauncher.apps.domain.ports.IconStyleStore
import com.paraskcd.influentiallauncher.apps.domain.ports.InstalledApps
import com.paraskcd.influentiallauncher.apps.infrastructure.DataStoreIconStyleStore
import com.paraskcd.influentiallauncher.apps.infrastructure.LauncherAppsInstalledApps
import com.paraskcd.influentiallauncher.apps.infrastructure.iconpack.AndroidIconPacks
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppsModule {
    @Binds
    abstract fun bindInstalledApps(impl: LauncherAppsInstalledApps): InstalledApps

    @Binds
    abstract fun bindIconStyleStore(impl: DataStoreIconStyleStore): IconStyleStore

    @Binds
    abstract fun bindIconPacks(impl: AndroidIconPacks): IconPacks
}
