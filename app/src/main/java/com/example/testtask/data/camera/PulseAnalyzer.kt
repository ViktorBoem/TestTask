package com.example.testtask.data.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.LinkedList
import java.util.concurrent.atomic.AtomicBoolean

class PulseAnalyzer(
    private val onBpmUpdate: (Int) -> Unit,
    private val onFingerDetectionChange: (Boolean) -> Unit,
    private val viewModelScope: CoroutineScope
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "PulseAnalyzer"

        private const val RED_THRESHOLD = 130
        private const val RED_DOMINANCE_FACTOR = 1.2f
        private const val BRIGHTNESS_THRESHOLD = 70
        private const val CONSISTENCY_FRAMES = 5
        private const val COVERAGE_THRESHOLD = 0.75f

        private const val MEASUREMENT_WINDOW_SIZE = 300
        private const val MIN_PEAKS_REQUIRED = 5
        private const val PEAK_DETECTION_THRESHOLD = 0.6
        private const val PROCESS_EVERY_N_FRAMES = 2
    }

    private val _isFingerDetected = MutableStateFlow(false)
    val isFingerDetected: StateFlow<Boolean> = _isFingerDetected.asStateFlow()

    private val _currentBpm = MutableStateFlow(0)
    val currentBpm: StateFlow<Int> = _currentBpm.asStateFlow()

    private var consistentDetectionCounter = 0
    private var consistentRemovalCounter = 0

    private var frameCounter = 0

    private val redIntensityValues = LinkedList<Float>()
    private val timeStamps = LinkedList<Long>()
    private var startTime = 0L
    private var isMeasuring = AtomicBoolean(false)

    private val smoothingWindow = ArrayList<Float>()
    private val smoothingWindowSize = 5

    override fun analyze(imageProxy: ImageProxy) {
        frameCounter = (frameCounter + 1) % PROCESS_EVERY_N_FRAMES

        if (frameCounter != 0) {
            imageProxy.close()
            return
        }

        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            bitmap?.let { bmp ->
                val isFingerPresent = analyzeImageForFinger(bmp)

                if (isFingerPresent && _isFingerDetected.value) {
                    analyzeImageForPulse(bmp)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in image analysis", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun analyzeImageForFinger(bitmap: Bitmap): Boolean {
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
                val pixel = bitmap.getPixel(x, y)

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

        val currentDetection = isRedDominant && isBright && hasCoverage

        updateDetectionState(currentDetection)

        return currentDetection
    }

    private fun analyzeImageForPulse(bitmap: Bitmap) {
        if (!isMeasuring.get()) {
            startMeasurementSession()
        }

        val avgRedIntensity = calculateAverageRedIntensity(bitmap)

        val currentTime = System.currentTimeMillis()
        redIntensityValues.add(avgRedIntensity)
        timeStamps.add(currentTime)

        while (redIntensityValues.size > MEASUREMENT_WINDOW_SIZE) {
            redIntensityValues.removeFirst()
            timeStamps.removeFirst()
        }

        if (redIntensityValues.size >= MEASUREMENT_WINDOW_SIZE / 2) {
            calculateBPM()
        }
    }

    private fun calculateAverageRedIntensity(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height

        val centerWidth = width / 2
        val centerHeight = height / 2
        val sampleRadius = min(width, height) / 4

        var redSum = 0
        var count = 0

        for (y in centerHeight - sampleRadius until centerHeight + sampleRadius) {
            for (x in centerWidth - sampleRadius until centerWidth + sampleRadius) {
                if (y >= 0 && y < height && x >= 0 && x < width) {
                    val pixel = bitmap.getPixel(x, y)
                    redSum += Color.red(pixel)
                    count++
                }
            }
        }

        val rawRedAvg = redSum.toFloat() / count

        smoothingWindow.add(rawRedAvg)
        if (smoothingWindow.size > smoothingWindowSize) {
            smoothingWindow.removeAt(0)
        }

        return smoothingWindow.average().toFloat()
    }

    private fun calculateBPM() {
        val filteredValues = lowPassFilter(redIntensityValues.toList())

        val peaks = findPeaks(filteredValues)

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
                    viewModelScope.launch {
                        _currentBpm.emit(beatsPerMinute)
                        onBpmUpdate(beatsPerMinute)
                    }
                }
            }
        }
    }

    private fun lowPassFilter(values: List<Float>): List<Float> {
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

    private fun findPeaks(values: List<Float>): List<Int> {
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

    private fun calculateStdDev(values: List<Float>, mean: Double): Double {
        if (values.isEmpty()) return 0.0

        var sumSquaredDiff = 0.0
        for (value in values) {
            sumSquaredDiff += (value - mean) * (value - mean)
        }

        return Math.sqrt(sumSquaredDiff / values.size)
    }

    private fun updateDetectionState(currentDetection: Boolean) {
        if (currentDetection) {
            consistentDetectionCounter++
            consistentRemovalCounter = 0
        } else {
            consistentRemovalCounter++
            consistentDetectionCounter = 0
        }

        val currentState = _isFingerDetected.value

        if (!currentState && consistentDetectionCounter >= CONSISTENCY_FRAMES) {
            viewModelScope.launch {
                _isFingerDetected.emit(true)
                onFingerDetectionChange(true)
            }
        } else if (currentState && consistentRemovalCounter >= CONSISTENCY_FRAMES) {
            viewModelScope.launch {
                _isFingerDetected.emit(false)
                onFingerDetectionChange(false)
                stopMeasurementSession()
            }
        }
    }

    private fun startMeasurementSession() {
        isMeasuring.set(true)
        startTime = System.currentTimeMillis()
        redIntensityValues.clear()
        timeStamps.clear()

        viewModelScope.launch {
            _currentBpm.emit(0)
        }
    }

    private fun stopMeasurementSession() {
        isMeasuring.getAndSet(false)
    }

    private fun min(a: Int, b: Int): Int {
        return if (a < b) a else b
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(
            nv21,
            ImageFormat.NV21,
            imageProxy.width,
            imageProxy.height,
            null
        )

        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            Rect(0, 0, imageProxy.width, imageProxy.height),
            100,
            out
        )

        val imageBytes = out.toByteArray()

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}
