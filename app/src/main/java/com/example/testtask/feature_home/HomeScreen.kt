package com.example.testtask.feature_home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import com.example.testtask.feature_home.components.HomePageTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.testtask.R
import  com.example.testtask.components.*
import com.example.testtask.feature_home.components.CircleImageButtonWithGradient
import com.example.testtask.ui.theme.*

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { HomePageTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                HeightSizedSquareWithRoundedBottom(
                    modifier = Modifier.fillMaxHeight()
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Виконайте своє перше вимірювання!",
                        style = Typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.heart_rate_red),
                        contentDescription = "Серце з пульсом",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .weight(3f)
                    )
                }
            }

            BoxWithConstraints(modifier = Modifier
                .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircleImageButtonWithGradient(
                    painter = painterResource(id = R.drawable.heart_rate_white),
                    contentDescription = "Серце з пульсом",
                    primaryColor = CoralPink,
                    secondaryColor = Folly,
                    modifier = Modifier.size(maxHeight * 0.75f),
                    onClick = { })
            }
        }
    }
}