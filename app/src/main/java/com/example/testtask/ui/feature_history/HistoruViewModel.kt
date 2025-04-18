package com.example.testtask.ui.feature_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.local.model.PulseRecord
import com.example.testtask.domain.repository.IPulseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val records: List<PulseRecord> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: IPulseRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.getAllPulseRecords()
        .map { records -> HistoryUiState(records = records, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = HistoryUiState(isLoading = true)
        )

    fun clearHistory() {
        viewModelScope.launch {
            repository.deleteAllPulseRecords()
        }
    }
}