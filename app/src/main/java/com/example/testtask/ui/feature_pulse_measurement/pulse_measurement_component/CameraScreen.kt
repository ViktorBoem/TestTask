package com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.Manifest
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import com.example.testtask.ui.feature_pulse_measurement.PulseMeasurementViewModel
import com.google.accompanist.permissions.*
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(modifier: Modifier = Modifier,
                 viewModel: PulseMeasurementViewModel = viewModel())
{
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(key1 = Unit) {
        if (!cameraPermissionState.status.isGranted && !cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            cameraPermissionState.status.isGranted -> {
                CameraPreviewContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
            cameraPermissionState.status.shouldShowRationale -> {
                Text(
                    "Потрібен дозвіл на камеру для роботи цієї функції. Будь ласка, надайте його в налаштуваннях.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                Text(
                    "Надайте дозвіл на використання камери",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}