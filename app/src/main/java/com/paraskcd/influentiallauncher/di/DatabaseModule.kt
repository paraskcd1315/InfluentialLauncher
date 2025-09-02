package com.paraskcd.influentiallauncher.di

import android.content.Context
import androidx.room.Room
import com.paraskcd.influentiallauncher.data.db.AppDatabase
import com.paraskcd.influentiallauncher.data.db.dao.AppShortcutDao
import com.paraskcd.influentiallauncher.data.db.dao.LauncherScreenDao
import com.paraskcd.influentiallauncher.data.db.repositories.AppShortcutRepository
import com.paraskcd.influentiallauncher.data.db.repositories.LauncherScreenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "launcher.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAppShortcutDao(db: AppDatabase): AppShortcutDao {
        return db.appShortcutDao()
    }

    @Provides
    @Singleton
    fun provideAppShortcutRepository(dao: AppShortcutDao): AppShortcutRepository {
        return AppShortcutRepository(dao)
    }

    @Provides
    @Singleton
    fun provideLauncherScreenDao(db: AppDatabase): LauncherScreenDao {
        return db.launcherScreenDao()
    }

    @Provides
    @Singleton
    fun provideLauncherScreenRepository(dao: LauncherScreenDao): LauncherScreenRepository {
        return LauncherScreenRepository(dao)
    }
}