package com.example.testtask.di

import android.content.Context
import androidx.room.Room
import com.example.testtask.data.local.AppDatabase
import com.example.testtask.data.local.dao.PulseRecordDao
import com.example.testtask.data.repository.PulseRepository
import com.example.testtask.domain.repository.IPulseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    companion object {
        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "pulse_database"
            ).build()
        }

        @Provides
        fun providePulseRecordDao(appDatabase: AppDatabase): PulseRecordDao {
            return appDatabase.pulseRecordDao()
        }
    }


    @Binds
    @Singleton
    abstract fun bindPulseRepository(
        pulseRepositoryImpl: PulseRepository
    ): IPulseRepository

}