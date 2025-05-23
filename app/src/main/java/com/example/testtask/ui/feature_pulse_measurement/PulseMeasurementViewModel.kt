package com.example.testtask.ui.feature_pulse_measurement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.domain.repository.IPulseRepository
import com.example.testtask.ui.feature_pulse_measurement.measurment_data_object.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import javax.inject.Inject

@HiltViewModel
class PulseMeasurementViewModel @Inject constructor(
    private val pulseRepository: IPulseRepository
) : ViewModel() {

    companion object {
        private const val TOTAL_MEASUREMENT_TIME_MS = 75_000L
        private const val PROGRESS_UPDATE_INTERVAL_MS = 100L
    }

    private val _uiState = MutableStateFlow(PulseUiState())
    val uiState: StateFlow<PulseUiState> = _uiState.asStateFlow()

    private var measurementJob: Job? = null
    private var lastValidBpm: Int = 0

    fun onFingerDetectionChange(isFingerDetected: Boolean) {
        if (isFingerDetected) {
            if (_uiState.value.status != MeasurementStatus.MeasurementPulse) {
                lastValidBpm = 0

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
            if (_uiState.value.status == MeasurementStatus.MeasurementPulse) {
                stopMeasurementTimer()

                _uiState.update {
                    it.copy(
                        status = MeasurementStatus.DetectingFinger,
                        title = "Палець не виявлено",
                        subtitle = "Щільно прикладіть палець до камери",
                        bpmValue = "--",
                        progress = 0.0f
                    )
                }
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
        measurementJob?.cancel()
        measurementJob = null
    }

    private fun onMeasurementComplete() {
        stopMeasurementTimer()

        val bpmToSave = lastValidBpm
        val timestamp = System.currentTimeMillis()

        if (bpmToSave > 0) {
            viewModelScope.launch {
                pulseRepository.savePulseRecord(bpmToSave, timestamp)

                _uiState.update {
                    it.copy(status = MeasurementStatus.MeasurementCompleted)
                }
            }
        }
        else {
            _uiState.update {
                it.copy(
                    status = MeasurementStatus.DetectingFinger,
                    title = "Спробуйте ще раз",
                    subtitle = "Щільно прикладіть палець до камери",
                    bpmValue = "--",
                    progress = 0.0f
                )
            }
        }
    }

    fun onBpmUpdate(bpm: Int) {
        if (_uiState.value.status == MeasurementStatus.MeasurementPulse) {
            val bpmText: String

            if (bpm > 0) {
                lastValidBpm = bpm
                bpmText = bpm.toString()
            }
            else {
                bpmText = _uiState.value.bpmValue
            }

            _uiState.update {
                it.copy(bpmValue = bpmText)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopMeasurementTimer()
    }
}