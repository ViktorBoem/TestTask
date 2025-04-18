package com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component

import android.util.Log
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
import androidx.camera.core.CameraUnavailableException
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.testtask.data.camera.PulseAnalyzer
import com.example.testtask.ui.feature_pulse_measurement.PulseMeasurementViewModel
import java.util.concurrent.Executors
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

private const val TAG = "CameraPreviewContent"

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
        val mainExecutor = ContextCompat.getMainExecutor(context)

        val previewUseCase = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        val imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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
            try {
                provider.unbindAll()

                camera = provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    imageAnalysisUseCase
                )

                camera?.cameraInfo?.hasFlashUnit()?.let { hasFlash ->
                    if (hasFlash) {
                        try {
                            camera?.cameraControl?.enableTorch(true)?.addListener({}, mainExecutor)
                        } catch (e: Exception) {
                            Log.w(TAG, "Не вдалося увімкнути ліхтарик", e)
                        }
                    }
                }
                Log.i(TAG, "Камера успішно прив'язана до життєвого циклу.")

            } catch (e: CameraUnavailableException) {
                Log.e(TAG, "Помилка прив'язки камери: Камера недоступна (можливо, немає дозволу або використовується іншим додатком).", e)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Помилка прив'язки камери: Неправильні аргументи або конфігурація.", e)
            } catch (e: Exception) {
                Log.e(TAG, "Невідома помилка при прив'язці камери до життєвого циклу", e)
            }
        }

        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()

                bindCamera(provider)
            } catch (e: ExecutionException) {
                Log.e(TAG, "Помилка при отриманні CameraProvider", e.cause ?: e)
            } catch (e: InterruptedException) {
                Log.e(TAG, "Отримання CameraProvider перервано", e)
                Thread.currentThread().interrupt()
            } catch (e: Exception) {
                Log.e(TAG, "Ловлю помилки від CameraProvider, як йобнутий", e)
            }
        }, mainExecutor)

        onDispose {
            Log.d(TAG, "DisposableEffect: Очищення ресурсів камери.")
            try {
                camera?.cameraInfo?.hasFlashUnit()?.let { hasFlash ->
                    if (hasFlash && camera?.cameraInfo?.torchState?.value == androidx.camera.core.TorchState.ON) {
                        camera?.cameraControl?.enableTorch(false)
                    }
                }

                cameraProviderFuture.get()?.unbindAll()
            } catch (e: Exception) {
                Log.e(TAG, "Помилка під час unbindAll або вимкнення ліхтарика в onDispose", e)
            } finally {
                cameraExecutor.shutdown()
            }
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier.clip(CircleShape)
    )
}