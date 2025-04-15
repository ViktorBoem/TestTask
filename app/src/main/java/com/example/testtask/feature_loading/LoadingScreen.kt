package com.example.testtask.feature_loading

import android.widget.Space
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import  com.example.testtask.components.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testtask.feature_loading.loading_component.CustomProgressBar
import com.example.testtask.R
import com.example.testtask.ui.theme.*

@Composable
fun LoadingScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoadingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.status) {
        if (uiState.status == InitializationState.Completed) {
            onNavigateToHome()
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .weight(4f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HeightSizedSquareWithRoundedBottom(
                modifier = Modifier.fillMaxHeight()
            )

            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.heart_rate_red),
                    contentDescription = "Серце з пульсом",
                    modifier = Modifier.fillMaxWidth(0.75f),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Heart Rate",
                    style = Typography.displayMedium
                )

            }
        }

        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(0.8f),
            contentAlignment = Alignment.Center
        ) {
            CustomProgressBar(progress = uiState.progress)
        }
    }
}