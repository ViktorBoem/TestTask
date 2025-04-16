package com.example.testtask.ui.feature_pulse_measurement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

enum class MeasurementStatus {
    DetectingFinger,
    MeasurementPulse,
    MeasurementCompleted
}

data class PulseUiState(
    val status: MeasurementStatus = MeasurementStatus.DetectingFinger,
    val title: String = "Палець не виявлено",
    val subtitle: String = "Щільно прикладіть палець до камери",
    val bpmValue: String = "--",
    val progress: Float = 0.0f
)

class PulseMeasurementViewModel : ViewModel() {

    companion object {
        private const val TOTAL_MEASUREMENT_TIME_MS = 75_000L
        private const val PROGRESS_UPDATE_INTERVAL_MS = 100L
    }

    private val _uiState = MutableStateFlow(PulseUiState())
    val uiState: StateFlow<PulseUiState> = _uiState.asStateFlow()

    private var measurementJob: Job? = null

    fun onFingerDetectionChange(isFingerDetected: Boolean) {
        if (isFingerDetected) {
            if (_uiState.value.status != MeasurementStatus.MeasurementPulse) {
                startMeasurementTimer()

                _uiState.update {
                    it.copy(
                        status = MeasurementStatus.MeasurementPulse,
                        title = "Йде Вимірювання.",
                        subtitle = "Визначаємо ваш пульс. Утримуйте!",
                        progress = 0.0f
                    )
                }
            }
        }
        else {
            stopMeasurementTimer()

            _uiState.update {
                it.copy(
                    status = MeasurementStatus.DetectingFinger,
                    title = "Палець не виявлено Блять",
                    subtitle = "Щільно прикладіть палець до камери",
                    bpmValue = "--",
                    progress = 0.0f
                )
            }
        }
    }

    private fun startMeasurementTimer() {
        measurementJob?.cancel()

        measurementJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            while (isActive) {
                val elapsedTime = System.currentTimeMillis() - startTime
                val currentProgress = (elapsedTime.toFloat() / TOTAL_MEASUREMENT_TIME_MS).coerceIn(0.0f, 1.0f)

                _uiState.update { it.copy(progress = currentProgress) }

                if (elapsedTime >= TOTAL_MEASUREMENT_TIME_MS) {
                    onMeasurementComplete()
                    break
                }

                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun stopMeasurementTimer() {
        if (measurementJob?.isActive == true) {
            measurementJob?.cancel()
        }

        measurementJob = null
    }

    private fun onMeasurementComplete() {
        stopMeasurementTimer()

        _uiState.update {
            it.copy(
                status = MeasurementStatus.MeasurementCompleted
            )
        }
    }

    fun onBpmUpdate(bpm: Int) {
        if (_uiState.value.status == MeasurementStatus.MeasurementPulse) {
            viewModelScope.launch {
                val bpmText = if (bpm > 0) {
                    bpm.toString()
                }
                else {
                    _uiState.value.bpmValue
                }
                if (bpm > 0) {
                    _uiState.update {
                        it.copy(bpmValue = bpmText)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMeasurementTimer()
    }
}