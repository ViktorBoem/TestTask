package com.example.testtask.ui.feature_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.domain.repository.IPulseRepository
import com.example.testtask.ui.feature_result.result_data_object.ResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val pulseRepository: IPulseRepository
) : ViewModel() {

    val uiState: StateFlow<ResultUiState> = pulseRepository.getLatestPulseRecord()
        .map { pulseRecord ->
            if (pulseRecord != null) {
                ResultUiState(
                    bpm = pulseRecord.bpm,
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