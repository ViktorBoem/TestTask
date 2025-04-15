package com.example.testtask.feature_loading.loading_component

import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.example.testtask.ui.theme.*

@Composable
fun CustomProgressBar(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = LightRed,
    trackColor: Color = Melon,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center)
    {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = BitterSweet,
                    shape = CircleShape),
            color = progressColor,
            trackColor = trackColor,
            strokeCap = Butt,
            gapSize = 0.dp,
            drawStopIndicator = {}
        )

        Text(
            text = "${(progress * 100).roundToInt()}%",
            color = textColor,
            style = Typography.bodyMedium
        )
    }
}