package com.example.testtask.ui.feature_pulse_measurement

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testtask.R
import com.example.testtask.ui.components.CustomProgressBar
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component.CameraScreen
import com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component.PressureMeasurementTopBar
import com.example.testtask.ui.theme.ArgentianBlue
import com.example.testtask.ui.theme.Typography
import com.example.testtask.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PressureMeasurementScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigationResult: () -> Unit,
    viewModel: PulseMeasurementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale_animation"
    )

    val currentScale = if (uiState.status == MeasurementStatus.MeasurementPulse) pulseScale else 1.0f

    LaunchedEffect(key1 = uiState.status) {
        if (uiState.status == MeasurementStatus.MeasurementCompleted) {
            onNavigationResult()
        }
    }

    Scaffold(
        topBar = { PressureMeasurementTopBar(onCloseClick = { onNavigateBack() }) },
        modifier = modifier,
    ) { _ ->

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PartialCircleBackground(
                    modifier = Modifier.fillMaxHeight()
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(64.dp))

                    CameraScreen(
                        modifier = Modifier
                            .size(64.dp)
                            .border(2.dp, ArgentianBlue, CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.title,
                        style = Typography.titleSmall,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = uiState.subtitle,
                        style = Typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = White
                    )

                    Box(
                        modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.heart_pulse_measurement),
                            contentDescription = "Серце з пульсом",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .graphicsLayer {
                                    scaleX = currentScale
                                    scaleY = currentScale
                                }
                        )

                        Text(
                            text = uiState.bpmValue,
                            style = Typography.displayLarge,
                            textAlign = TextAlign.Center,
                            color = White,
                            modifier = Modifier
                                .align(BiasAlignment(
                                    horizontalBias = 0f,
                                    verticalBias = -0.25f)
                                )
                                .graphicsLayer {
                                    scaleX = currentScale
                                    scaleY = currentScale
                                }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (uiState.status == MeasurementStatus.DetectingFinger){
                            Image(
                                painter = painterResource(id = R.drawable.hands_and_phone),
                                contentDescription = "палець прикладений до камери телефону",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .align(
                                        BiasAlignment(
                                            horizontalBias = 0.5f,
                                            verticalBias = 0f)
                                    )
                            )
                        }
                        else{
                            CustomProgressBar(progress = uiState.progress,
                                modifier = Modifier.fillMaxWidth(0.8f))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

    }

}