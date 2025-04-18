package com.example.testtask.domain.analysis

import com.example.testtask.domain.processing.SignalProcessor

class PulseCalculator {
    companion object {
        private const val MIN_PEAKS_REQUIRED = 5
        private const val PEAK_DETECTION_THRESHOLD = 0.6
    }

    private val signalProcessor = SignalProcessor()

    fun calculateBPM(redValues: List<Float>, timeStamps: List<Long>): Int {
        if (redValues.isEmpty() || timeStamps.isEmpty()) return 0

        val filteredValues = signalProcessor.lowPassFilter(redValues)
        val peaks = signalProcessor.findPeaks(filteredValues)

        if (peaks.size >= MIN_PEAKS_REQUIRED) {
            var totalTimeBetweenPeaks = 0L
            var count = 0

            for (i in 1 until peaks.size) {
                val timeDiff = timeStamps[peaks[i]] - timeStamps[peaks[i - 1]]
                if (timeDiff > 0) {
                    totalTimeBetweenPeaks += timeDiff
                    count++
                }
            }

            if (count > 0) {
                val avgTimeBetweenPeaksMs = totalTimeBetweenPeaks.toFloat() / count
                val beatsPerSecond = 1000f / avgTimeBetweenPeaksMs
                val beatsPerMinute = (beatsPerSecond * 60).toInt()

                if (beatsPerMinute in 40..200) {
                    return beatsPerMinute
                }
            }
        }

        return 0
    }
}