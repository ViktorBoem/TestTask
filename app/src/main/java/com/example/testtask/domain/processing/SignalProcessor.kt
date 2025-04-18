package com.example.testtask.domain.processing

import kotlin.math.sqrt

class SignalProcessor {
    companion object {
        private const val PEAK_DETECTION_THRESHOLD = 0.6
    }

    fun lowPassFilter(values: List<Float>): List<Float> {
        val alpha = 0.3f
        val result = ArrayList<Float>(values.size)

        if (values.isEmpty()) return result

        result.add(values[0])

        for (i in 1 until values.size) {
            val filtered = alpha * values[i] + (1 - alpha) * result[i - 1]
            result.add(filtered)
        }

        return result
    }

    fun findPeaks(values: List<Float>): List<Int> {
        val peaks = ArrayList<Int>()

        val mean = values.average()
        val stdDev = calculateStdDev(values, mean)

        val threshold = mean + PEAK_DETECTION_THRESHOLD * stdDev

        val minPeakDistance = 10

        for (i in 1 until values.size - 1) {
            if (values[i] > threshold &&
                values[i] > values[i - 1] &&
                values[i] > values[i + 1] &&
                (peaks.isEmpty() || i - peaks.last() > minPeakDistance)
            ) {
                peaks.add(i)
            }
        }

        return peaks
    }

    fun calculateStdDev(values: List<Float>, mean: Double): Double {
        if (values.isEmpty()) return 0.0

        var sumSquaredDiff = 0.0
        for (value in values) {
            sumSquaredDiff += (value - mean) * (value - mean)
        }

        return sqrt(sumSquaredDiff / values.size)
    }
}
