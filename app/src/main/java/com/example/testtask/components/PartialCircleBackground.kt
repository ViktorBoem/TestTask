package com.example.testtask.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.example.testtask.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.core.util.rangeTo
import kotlin.math.roundToInt

@Composable
fun HeightSizedSquareWithRoundedBottom(
    modifier: Modifier = Modifier,
    color: Color = UranianBlue,
    bottomCornerPercent: Int = 100
) {
    val shape = RoundedCornerShape(
        topStartPercent = 0,
        topEndPercent = 0,
        bottomStartPercent = bottomCornerPercent,
        bottomEndPercent = bottomCornerPercent
    )

    Layout(
        content = {
            Box(
                modifier = Modifier
                    .background(color = color, shape = shape)
            )
        },
        modifier = modifier
    ) { measurables, constraints ->
        val height = maxOf(constraints.maxHeight, constraints.maxWidth)
        val width = (height * 1.25).roundToInt()

        val placeable = measurables[0].measure(
            Constraints.fixed(width = width, height = height)
        )

        layout(width, height) {
            placeable.place(0, 0)
        }
    }
}