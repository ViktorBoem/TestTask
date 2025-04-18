package com.example.testtask.data.repository

import com.example.testtask.data.local.dao.PulseRecordDao
import com.example.testtask.data.local.model.PulseRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PulseRepository @Inject constructor(
    private val pulseRecordDao: PulseRecordDao
) {

    suspend fun savePulseRecord(bpm: Int, timestamp: Long) {
        val record = PulseRecord(bpm = bpm, timestamp = timestamp)
        pulseRecordDao.insertPulseRecord(record)
    }

    fun getLatestPulseRecord(): Flow<PulseRecord?> {
        return pulseRecordDao.getLatestPulseRecord()
    }
}