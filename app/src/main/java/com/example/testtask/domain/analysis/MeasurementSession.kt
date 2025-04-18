package com.example.testtask.domain.analysis

import java.util.LinkedList

class MeasurementSession {
    companion object {
        private const val MEASUREMENT_WINDOW_SIZE = 300
    }

    private val redIntensityValues = LinkedList<Float>()
    private val timeStamps = LinkedList<Long>()
    private val startTime = System.currentTimeMillis()

    fun addDataPoint(redIntensity: Float, timestamp: Long) {
        redIntensityValues.add(redIntensity)
        timeStamps.add(timestamp)

        while (redIntensityValues.size > MEASUREMENT_WINDOW_SIZE) {
            redIntensityValues.removeFirst()
            timeStamps.removeFirst()
        }
    }

    fun hasEnoughData(): Boolean = redIntensityValues.size >= MEASUREMENT_WINDOW_SIZE / 2

    fun getRedValues(): List<Float> = redIntensityValues.toList()

    fun getTimeStamps(): List<Long> = timeStamps.toList()
}