package com.example.testtask.ui.feature_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.repository.PulseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ResultUiState(
    val bpm: Int = 0,
    val formattedTime: String = "",
    val formattedDate: String = "",
    val timestamp: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val pulseRepository: PulseRepository
) : ViewModel() {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val uiState: StateFlow<ResultUiState> = pulseRepository.getLatestPulseRecord()
        .map { pulseRecord ->
            if (pulseRecord != null) {
                val dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(pulseRecord.timestamp),
                    ZoneId.systemDefault()
                )
                ResultUiState(
                    bpm = pulseRecord.bpm,
                    formattedTime = dateTime.format(timeFormatter),
                    formattedDate = dateTime.format(dateFormatter),
                    timestamp = pulseRecord.timestamp,
                    isLoading = false
                )
            } else {
                ResultUiState(
                    isLoading = false,
                    error = "Немає даних про вимірювання"
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ResultUiState(isLoading = true)
        )
}