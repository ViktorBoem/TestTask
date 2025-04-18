package com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import androidx.camera.core.Camera
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testtask.data.camera.PulseAnalyzer
import com.example.testtask.ui.feature_pulse_measurement.PulseMeasurementViewModel
import java.util.concurrent.Executors
import kotlinx.coroutines.launch

@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: PulseMeasurementViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    var camera: Camera? by remember { mutableStateOf(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val executor = ContextCompat.getMainExecutor(context)

        val previewUseCase = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(previewView.display.rotation)
            .build()

        val pulseAnalyzer = PulseAnalyzer(
            onBpmUpdate = { bpm ->
                coroutineScope.launch {
                    viewModel.onBpmUpdate(bpm)
                }
            },
            onFingerDetectionChange = { isDetected ->
                coroutineScope.launch {
                    viewModel.onFingerDetectionChange(isDetected)
                }
            },
            viewModelScope = viewModel.viewModelScope
        )

        imageAnalysisUseCase.setAnalyzer(cameraExecutor, pulseAnalyzer)

        fun bindCamera(provider: ProcessCameraProvider) {
            provider.unbindAll()
            camera = provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageAnalysisUseCase
            )

            camera?.cameraInfo?.hasFlashUnit()?.let { hasFlash ->
                if (hasFlash) {
                    camera?.cameraControl?.enableTorch(true)
                }
            }
        }

        cameraProviderFuture.addListener({
            cameraProviderFuture.get()?.let { provider ->
                bindCamera(provider)
            }
        }, executor)

        onDispose {
            camera?.cameraInfo?.hasFlashUnit()?.let { hasFlash ->
                if (hasFlash) {
                    camera?.cameraControl?.enableTorch(false)
                }
            }
            cameraProviderFuture.get()?.unbindAll()
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.clip(CircleShape)
    )
}