package com.example.testtask.ui.feature_result.result_component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.testtask.ui.theme.Silver
import com.example.testtask.ui.theme.White
import kotlin.math.max
import kotlin.math.min

@Composable
fun SegmentedValueIndicator(
    value: Int,
    valueRange: IntRange,
    breakpoints: List<Int>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp,
    indicatorWidth: Dp = 5.dp,
    indicatorHeight: Dp = trackHeight,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round,
    indicatorColor: Color = White,
    indicatorBorderWidth: Dp = 1.dp,
    indicatorBorderColor: Color = Silver
) {
    val trackHeightPx = with(LocalDensity.current) { trackHeight.toPx() }
    val indicatorWidthPx = with(LocalDensity.current) { indicatorWidth.toPx() }
    val indicatorHeightPx = with(LocalDensity.current) { indicatorHeight.toPx() }
    val indicatorBorderWidthPx = with(LocalDensity.current) { indicatorBorderWidth.toPx() }
    val capRadius = trackHeightPx / 2f
    val halfIndicatorHeightPx = indicatorHeightPx / 2f

    var animate by remember { mutableStateOf(false) }

    val targetValueRatio = remember(value, valueRange) {
        val rangeSpan = (valueRange.last - valueRange.first).toFloat()
        if (rangeSpan > 0) {
            ((value - valueRange.first).toFloat() / rangeSpan).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    val animatedRatio by animateFloatAsState(
        targetValue = if (animate) targetValueRatio else 0f,
        animationSpec = tween(durationMillis = 3000),
        label = "valueRatioAnimation"
    )

    LaunchedEffect(Unit) {
        animate = true
    }

    val segments = remember(valueRange, breakpoints, colors) {
        buildList {
            var currentStart: Int = valueRange.first

            breakpoints.forEachIndexed { index, breakpoint ->
                val validBreakpoint = breakpoint.coerceIn(valueRange)
                if (validBreakpoint > currentStart) {
                    add(Triple(currentStart, validBreakpoint, colors.getOrElse(index) { colors.last() }))
                    currentStart = validBreakpoint
                }
            }

            if (currentStart <= valueRange.last) {
                val lastColorIndex = (breakpoints.size).coerceAtMost(colors.size - 1)
                add(Triple(currentStart, valueRange.last, colors[lastColorIndex]))
            }
        }
    }

    val requiredCanvasHeight = max(trackHeight.value, indicatorHeight.value).dp

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(requiredCanvasHeight)
    ) {
        val canvasWidth = size.width
        val yCenter = size.height / 2f
        val rangeSpanForDrawing = (valueRange.last - valueRange.first).toFloat()

        segments.forEachIndexed { index, (startValue, endValue, color) ->
            val startRatio = if (rangeSpanForDrawing > 0) ((startValue - valueRange.first).toFloat() / rangeSpanForDrawing) else 0f
            val endRatio = if (rangeSpanForDrawing > 0) ((endValue - valueRange.first).toFloat() / rangeSpanForDrawing) else 1f

            val startPx = startRatio.coerceIn(0f, 1f) * canvasWidth
            val endPx = endRatio.coerceIn(0f, 1f) * canvasWidth

            val actualStartPx = if (index == 0) max(startPx, capRadius) else startPx
            val actualEndPx = if (index == segments.lastIndex) min(endPx, canvasWidth - capRadius) else endPx


            if (actualEndPx > actualStartPx) {
                drawLine(
                    color = color,
                    start = Offset(actualStartPx, yCenter),
                    end = Offset(actualEndPx, yCenter),
                    strokeWidth = trackHeightPx,
                    cap = StrokeCap.Butt
                )
            }
        }

        if (segments.isNotEmpty()) {
            drawCircle(
                color = segments.first().third,
                radius = capRadius,
                center = Offset(capRadius, yCenter)
            )
            drawCircle(
                color = segments.last().third,
                radius = capRadius,
                center = Offset(canvasWidth - capRadius, yCenter)
            )
        }
        else if (rangeSpanForDrawing == 0f && colors.isNotEmpty()){
            val singleColor = colors.first()
            drawLine(
                color = singleColor,
                start = Offset(capRadius, yCenter),
                end = Offset(canvasWidth - capRadius, yCenter),
                strokeWidth = trackHeightPx,
                cap = StrokeCap.Butt
            )
            drawCircle(color = singleColor, radius = capRadius, center = Offset(capRadius, yCenter))
            drawCircle(color = singleColor, radius = capRadius, center = Offset(canvasWidth - capRadius, yCenter))
        }


        val indicatorXPx = animatedRatio * (canvasWidth - 2 * capRadius) + capRadius
        val clampedIndicatorXPx = indicatorXPx.coerceIn(capRadius, canvasWidth - capRadius)

        if (indicatorBorderWidth > 0.dp) {
            val borderLineStrokeWidth = indicatorWidthPx + 2 * indicatorBorderWidthPx
            drawLine(
                color = indicatorBorderColor,
                start = Offset(clampedIndicatorXPx, yCenter - halfIndicatorHeightPx),
                end = Offset(clampedIndicatorXPx, yCenter + halfIndicatorHeightPx),
                strokeWidth = borderLineStrokeWidth,
                cap = indicatorStrokeCap
            )
        }

        drawLine(
            color = indicatorColor,
            start = Offset(clampedIndicatorXPx, yCenter - halfIndicatorHeightPx),
            end = Offset(clampedIndicatorXPx, yCenter + halfIndicatorHeightPx),
            strokeWidth = indicatorWidthPx,
            cap = indicatorStrokeCap
        )
    }
}