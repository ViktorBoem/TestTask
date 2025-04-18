package com.example.testtask.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pulse_records")
data class PulseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bpm: Int,
    val timestamp: Long
)