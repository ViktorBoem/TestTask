package com.example.testtask.ui.feature_result.result_data_object

data class ResultUiState(
    val bpm: Int = 0,
    val timestamp: Long = 0L,
    val isLoading: Boolean = true,
    val error: String? = null
)