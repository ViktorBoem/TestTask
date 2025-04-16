package com.example.testtask.ui.feature_loading

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testtask.ui.components.CustomProgressBar
import com.example.testtask.R
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.theme.*

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    onNavigateToOnboarding: () -> Unit,
    viewModel: LoadingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.status) {
        if (uiState.status == InitializationState.Completed) {
            onNavigateToOnboarding()
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
            PartialCircleBackground(
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

                Spacer(modifier = Modifier.height(8.dp))

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