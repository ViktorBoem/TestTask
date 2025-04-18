package com.example.testtask.ui.feature_history.history_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.testtask.ui.theme.LightRed

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = LightRed,
    thickness: Dp = 5.dp
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
            .background(
                color = color,
                shape = CircleShape
            )
            .height(75.dp)
    )
}