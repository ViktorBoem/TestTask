package com.example.testtask.data.repository

import com.example.testtask.data.local.dao.PulseRecordDao
import com.example.testtask.data.local.model.PulseRecord
import com.example.testtask.domain.repository.IPulseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PulseRepository @Inject constructor(
    private val pulseRecordDao: PulseRecordDao
) : IPulseRepository {

    override suspend fun savePulseRecord(bpm: Int, timestamp: Long) {
        val record = PulseRecord(bpm = bpm, timestamp = timestamp)
        pulseRecordDao.insertPulseRecord(record)
    }

    override fun getLatestPulseRecord(): Flow<PulseRecord?> {
        return pulseRecordDao.getLatestPulseRecord()
    }

    override fun getAllPulseRecords(): Flow<List<PulseRecord>> {
        return pulseRecordDao.getAllPulseRecords()
    }

    override suspend fun deleteAllPulseRecords() {
        pulseRecordDao.deleteAllPulseRecords()
    }
}