package com.example.testtask.data.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.testtask.domain.analysis.FingerDetector
import com.example.testtask.domain.analysis.MeasurementSession
import com.example.testtask.domain.analysis.PulseCalculator
import com.example.testtask.domain.processing.ImageProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PulseAnalyzer(
    private val onBpmUpdate: (Int) -> Unit,
    private val onFingerDetectionChange: (Boolean) -> Unit,
    private val viewModelScope: CoroutineScope
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "PulseAnalyzer"
        private const val PROCESS_EVERY_N_FRAMES = 2
    }

    private val _isFingerDetected = MutableStateFlow(false)
    private val _currentBpm = MutableStateFlow(0)

    private var frameCounter = 0
    private val imageProcessor = ImageProcessor()
    private val fingerDetector = FingerDetector()
    private val pulseCalculator = PulseCalculator()
    private var measurementSession: MeasurementSession? = null

    override fun analyze(imageProxy: ImageProxy) {
        frameCounter = (frameCounter + 1) % PROCESS_EVERY_N_FRAMES

        if (frameCounter != 0) {
            imageProxy.close()
            return
        }

        try {
            val bitmap = imageProcessor.imageProxyToBitmap(imageProxy)
            bitmap?.let { bmp ->
                val isFingerPresent = fingerDetector.detectFinger(bmp)
                updateDetectionState(isFingerPresent)

                if (isFingerPresent && _isFingerDetected.value) {
                    measurePulse(bmp)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in image analysis", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun measurePulse(bitmap: Bitmap) {
        if (measurementSession == null) {
            measurementSession = MeasurementSession()
        }

        val session = measurementSession ?: return
        val avgRedIntensity = imageProcessor.calculateAverageRedIntensity(bitmap)

        val currentTime = System.currentTimeMillis()
        session.addDataPoint(avgRedIntensity, currentTime)

        if (session.hasEnoughData()) {
            val bpm = pulseCalculator.calculateBPM(
                session.getRedValues(),
                session.getTimeStamps()
            )

            if (bpm > 0) {
                viewModelScope.launch {
                    _currentBpm.emit(bpm)
                    onBpmUpdate(bpm)
                }
            }
        }
    }

    private fun updateDetectionState(currentDetection: Boolean) {
        val stateChanged = fingerDetector.updateDetectionState(currentDetection)

        if (stateChanged) {
            val newDetectionState = fingerDetector.isFingerDetected()

            viewModelScope.launch {
                _isFingerDetected.emit(newDetectionState)
                onFingerDetectionChange(newDetectionState)

                if (!newDetectionState) {
                    stopMeasurementSession()
                }
            }
        }
    }

    private fun stopMeasurementSession() {
        measurementSession = null

        viewModelScope.launch {
            _currentBpm.emit(0)
        }
    }
}