package com.example.testtask.ui.feature_home.home_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.testtask.ui.theme.*
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import com.example.testtask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageTopBar(modifier: Modifier = Modifier,
                   onNavigateToHistory : () -> Unit
) {
    TopAppBar(
        title = { },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Історія",
                    style = Typography.titleLarge,
                    color = White,
                    modifier = Modifier.clickable{ onNavigateToHistory() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.time_machine),
                    contentDescription = "іконка історії",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .clickable{ onNavigateToHistory() }
                )

                Spacer(modifier = Modifier.width(4.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LightRed,
            titleContentColor = White
        ),
        modifier = modifier
    )
}