package com.example.testtask.ui.feature_pulse_measurement.measurment_data_object

data class PulseUiState(
    val status: MeasurementStatus = MeasurementStatus.DetectingFinger,
    val title: String = "Палець не виявлено",
    val subtitle: String = "Щільно прикладіть палець до камери",
    val bpmValue: String = "--",
    val progress: Float = 0.0f
)