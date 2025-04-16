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
import android.util.Log
import androidx.camera.core.Camera
import androidx.lifecycle.LifecycleOwner
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
    viewModel: PulseMeasurementViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    var camera: Camera? by remember { mutableStateOf(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(key1 = cameraProviderFuture, key2 = lifecycleOwner, key3 = cameraSelector) {
        val executor = ContextCompat.getMainExecutor(context)
        var cameraProvider: ProcessCameraProvider? = null

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

        fun bindPreview(owner: LifecycleOwner, provider: ProcessCameraProvider) {
            try {
                provider.unbindAll()

                camera = provider.bindToLifecycle(
                    owner,
                    cameraSelector,
                    previewUseCase,
                    imageAnalysisUseCase
                )

                camera?.let { cam ->
                    if (cam.cameraInfo.hasFlashUnit()) {
                        cam.cameraControl.enableTorch(true)
                        Log.d("CameraPreview", "Torch enabled")
                    } else {
                        Log.d("CameraPreview", "No flash unit available")
                    }
                }

            } catch (exc: Exception) {
                camera = null
            }
        }

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                cameraProvider?.let { provider ->
                    bindPreview(lifecycleOwner, provider)
                }
            } catch (e: Exception) {
                Log.e("CameraPreview", "Camera provider listener failed", e)
            }
        }, executor)

        onDispose {
            try {
                camera?.let { cam ->
                    if (cam.cameraInfo.hasFlashUnit()) {
                        cam.cameraControl.enableTorch(false)
                    }
                }

                cameraProvider?.unbindAll()
                cameraExecutor.shutdown()
            } catch (e: Exception) {
                Log.e("CameraPreview", "Cleanup failed", e)
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
            .clip(CircleShape)
    )
}