package com.example.testtask.domain.repository

import com.example.testtask.data.local.model.PulseRecord
import kotlinx.coroutines.flow.Flow

interface IPulseRepository {
    suspend fun savePulseRecord(bpm: Int, timestamp: Long)

    fun getLatestPulseRecord(): Flow<PulseRecord?>

    fun getAllPulseRecords(): Flow<List<PulseRecord>>

    suspend fun deleteAllPulseRecords()
}