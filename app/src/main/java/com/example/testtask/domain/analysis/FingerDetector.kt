package com.example.testtask.domain.analysis

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.get

class FingerDetector {
    companion object {
        private const val RED_THRESHOLD = 130
        private const val RED_DOMINANCE_FACTOR = 1.2f
        private const val BRIGHTNESS_THRESHOLD = 70
        private const val CONSISTENCY_FRAMES = 5
        private const val COVERAGE_THRESHOLD = 0.75f
    }

    private var consistentDetectionCounter = 0
    private var consistentRemovalCounter = 0
    private var isFingerDetected = false

    fun detectFinger(bitmap: Bitmap): Boolean {
        val width = bitmap.width
        val height = bitmap.height

        val sampleSizeX = width / 20
        val sampleSizeY = height / 20

        var redSum = 0
        var greenSum = 0
        var blueSum = 0
        var fingerColorPixels = 0
        var sampledPixels = 0

        for (y in 0 until height step sampleSizeY) {
            for (x in 0 until width step sampleSizeX) {
                val pixel = bitmap[x, y]

                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                redSum += r
                greenSum += g
                blueSum += b

                val brightness = (r + g + b) / 3

                if (r > RED_THRESHOLD &&
                    r > g * RED_DOMINANCE_FACTOR &&
                    r > b * RED_DOMINANCE_FACTOR &&
                    brightness > BRIGHTNESS_THRESHOLD
                ) {
                    fingerColorPixels++
                }

                sampledPixels++
            }
        }

        val coverageRatio = fingerColorPixels.toFloat() / sampledPixels
        val avgRed = redSum / sampledPixels
        val avgGreen = greenSum / sampledPixels
        val avgBlue = blueSum / sampledPixels
        val avgBrightness = (avgRed + avgGreen + avgBlue) / 3

        val isRedDominant = avgRed > RED_THRESHOLD &&
                avgRed > avgGreen * RED_DOMINANCE_FACTOR &&
                avgRed > avgBlue * RED_DOMINANCE_FACTOR

        val isBright = avgBrightness > BRIGHTNESS_THRESHOLD
        val hasCoverage = coverageRatio > COVERAGE_THRESHOLD

        return isRedDominant && isBright && hasCoverage
    }

    fun updateDetectionState(currentDetection: Boolean): Boolean {
        val previousState = isFingerDetected

        if (currentDetection) {
            consistentDetectionCounter++
            consistentRemovalCounter = 0
        } else {
            consistentRemovalCounter++
            consistentDetectionCounter = 0
        }

        if (!isFingerDetected && consistentDetectionCounter >= CONSISTENCY_FRAMES) {
            isFingerDetected = true
        } else if (isFingerDetected && consistentRemovalCounter >= CONSISTENCY_FRAMES) {
            isFingerDetected = false
        }

        return previousState != isFingerDetected
    }

    fun isFingerDetected(): Boolean = isFingerDetected
}