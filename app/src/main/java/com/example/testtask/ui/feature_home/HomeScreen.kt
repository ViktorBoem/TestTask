package com.example.testtask.ui.feature_home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import com.example.testtask.ui.feature_home.home_component.HomePageTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.testtask.R
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.feature_home.home_component.CircleImageButtonWithGradient
import com.example.testtask.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onNavigateToPressureMeasurement : () -> Unit,
    onNavigateToHistory : () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = { HomePageTopBar(onNavigateToHistory = onNavigateToHistory) },
        modifier = modifier
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
            ){
                PartialCircleBackground(
                    modifier = Modifier.fillMaxHeight()
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = "Виконайте своє перше вимірювання!",
                            style = Typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(2.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.heart_rate_red),
                            contentDescription = "Серце з пульсом",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(
                                    BiasAlignment(
                                        horizontalBias = 0f,
                                        verticalBias = -0.3f)
                                )
                        )
                    }
                }
            }

            Box(modifier = Modifier
                .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircleImageButtonWithGradient(
                    painter = painterResource(id = R.drawable.heart_rate_white),
                    contentDescription = "Серце з пульсом",
                    primaryColor = CoralPink,
                    secondaryColor = Folly,
                    modifier = Modifier.size(150.dp * 0.75f),
                    onClick = onNavigateToPressureMeasurement)
            }
        }
    }
}