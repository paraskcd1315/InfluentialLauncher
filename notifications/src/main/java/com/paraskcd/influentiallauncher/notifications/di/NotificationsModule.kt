// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.notifications.di

import com.paraskcd.influentiallauncher.notifications.domain.ports.NotificationBadges
import com.paraskcd.influentiallauncher.notifications.infrastructure.ListenerNotificationBadges
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
    @Binds
    abstract fun bindNotificationBadges(impl: ListenerNotificationBadges): NotificationBadges
}
