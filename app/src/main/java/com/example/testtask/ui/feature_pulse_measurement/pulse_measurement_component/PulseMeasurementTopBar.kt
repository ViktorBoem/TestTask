package com.example.testtask.ui.feature_pulse_measurement.pulse_measurement_component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import com.example.testtask.ui.theme.*
import com.example.testtask.R
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PressureMeasurementTopBar(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit
) {
    TopAppBar(
        title = { },
        actions = {
            IconButton(onClick = { onCloseClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "Закрити",
                    tint = DimGray,
                    modifier = Modifier.fillMaxHeight(0.35f)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            actionIconContentColor = LocalContentColor.current
        ),
        modifier = modifier
    )
}