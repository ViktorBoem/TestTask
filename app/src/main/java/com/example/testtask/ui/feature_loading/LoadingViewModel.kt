package com.example.testtask.ui.feature_loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.min

data class LoadingUiState(
    val progress: Float = 0.0f,
    val status: InitializationState = InitializationState.Initializing
)

enum class InitializationState {
    Initializing,
    Completed
}

class LoadingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoadingUiState())
    val uiState: StateFlow<LoadingUiState> = _uiState.asStateFlow()

    init {
        startLoadingSimulation()
    }

    private fun startLoadingSimulation() {
        viewModelScope.launch {
            val durationMillis = 5000L
            val steps = 100
            val delayPerStep = durationMillis / steps

            for (i in 1..steps) {
                delay(delayPerStep)
                val currentProgress = min(1.0f, i.toFloat() / steps.toFloat())

                _uiState.update { currentState ->
                    currentState.copy(progress = currentProgress)
                }

                if (currentProgress >= 1.0f) break
            }

            _uiState.update { currentState ->
                currentState.copy(progress = 1.0f, status = InitializationState.Completed)
            }
        }
    }
}