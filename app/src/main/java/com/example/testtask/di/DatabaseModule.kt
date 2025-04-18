package com.example.testtask.di

import android.content.Context
import com.example.testtask.data.local.AppDatabase
import com.example.testtask.data.local.dao.PulseRecordDao
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
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    @Provides
    fun providePulseRecordDao(appDatabase: AppDatabase): PulseRecordDao {
        return appDatabase.pulseRecordDao()
    }
}