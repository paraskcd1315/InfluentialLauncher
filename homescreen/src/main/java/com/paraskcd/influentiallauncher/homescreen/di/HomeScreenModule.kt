package com.paraskcd.influentiallauncher.homescreen.di

import com.paraskcd.influentiallauncher.homescreen.domain.ports.HomeStore
import com.paraskcd.influentiallauncher.homescreen.infrastructure.DataStoreHomeStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeScreenModule {
    @Binds
    abstract fun bindHomeStore(impl: DataStoreHomeStore): HomeStore
}
