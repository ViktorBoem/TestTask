package com.example.testtask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testtask.data.local.model.PulseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PulseRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPulseRecord(record: PulseRecord)

    @Query("SELECT * FROM pulse_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPulseRecord(): Flow<PulseRecord?>

    @Query("SELECT * FROM pulse_records ORDER BY timestamp DESC")
    fun getAllPulseRecords(): Flow<List<PulseRecord>>

    @Query("DELETE FROM pulse_records")
    suspend fun deleteAllPulseRecords()
}