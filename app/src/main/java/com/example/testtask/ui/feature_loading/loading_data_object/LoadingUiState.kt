package com.example.testtask.ui.feature_loading.loading_data_object

data class LoadingUiState(
    val progress: Float = 0.0f,
    val status: InitializationState = InitializationState.Initializing
)