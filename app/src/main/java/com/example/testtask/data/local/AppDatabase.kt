package com.example.testtask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.testtask.data.local.dao.PulseRecordDao
import com.example.testtask.data.local.model.PulseRecord

@Database(entities = [PulseRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pulseRecordDao(): PulseRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pulse_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}