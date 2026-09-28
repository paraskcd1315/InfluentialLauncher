// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.timetracking.di

import com.paraskcd.influentiallauncher.timetracking.domain.ports.CredentialsStore
import com.paraskcd.influentiallauncher.timetracking.domain.ports.TrackerClient
import com.paraskcd.influentiallauncher.timetracking.infrastructure.DataStoreCredentialsStore
import com.paraskcd.influentiallauncher.timetracking.infrastructure.kimai.KimaiClient
import com.paraskcd.influentiallauncher.timetracking.infrastructure.toggl.TogglClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeTrackingModule {
    @Binds
    abstract fun bindCredentialsStore(impl: DataStoreCredentialsStore): CredentialsStore

    @Binds
    @IntoSet
    abstract fun bindToggl(impl: TogglClient): TrackerClient

    @Binds
    @IntoSet
    abstract fun bindKimai(impl: KimaiClient): TrackerClient
}
