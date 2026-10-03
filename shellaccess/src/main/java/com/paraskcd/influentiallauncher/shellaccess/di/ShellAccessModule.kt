// Copyright 2026 Paras Mohandas Khanchandani Chandani
// All rights reserved.

package com.paraskcd.influentiallauncher.shellaccess.di

import android.content.Context
import com.paraskcd.influentiallauncher.shellaccess.domain.ports.ShellAccess
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.ShellAccessImpl
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbKeyStore
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.AdbMdns
import com.paraskcd.influentiallauncher.shellaccess.infrastructure.adb.ShellStarter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShellAccessModule {
    @Provides
    @Singleton
    fun provideShellAccess(@ApplicationContext context: Context): ShellAccess =
        ShellAccessImpl(
            context = context,
            keyStore = AdbKeyStore(context),
            mdns = AdbMdns(context),
            starter = ShellStarter(context)
        )
}
