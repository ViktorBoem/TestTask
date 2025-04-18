package com.example.testtask.ui.feature_history.history_component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.testtask.R
import com.example.testtask.ui.theme.LightRed
import com.example.testtask.ui.theme.White
import androidx.compose.material3.Icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopAppBar(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Історія",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Назад",
                    tint = White
                )
            }
        },
        actions = {  },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LightRed,
            titleContentColor = White,
            navigationIconContentColor = White
        ),
        modifier = modifier
    )
}