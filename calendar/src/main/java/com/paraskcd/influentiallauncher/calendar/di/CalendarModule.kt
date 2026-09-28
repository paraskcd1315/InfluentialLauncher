// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.calendar.di

import com.paraskcd.influentiallauncher.calendar.domain.ports.CalendarSource
import com.paraskcd.influentiallauncher.calendar.infrastructure.CalendarContractSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarModule {
    @Binds
    abstract fun bindCalendarSource(impl: CalendarContractSource): CalendarSource
}
