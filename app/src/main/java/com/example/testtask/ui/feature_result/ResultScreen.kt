package com.example.testtask.ui.feature_result

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.feature_result.result_component.ResultTopAppBar
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.testtask.ui.theme.Typography
import androidx.compose.ui.unit.*
import com.example.testtask.ui.theme.TestTaskTheme
import java.time.*
import java.time.format.*
import com.example.testtask.R
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.testtask.ui.theme.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { ResultTopAppBar() },
        modifier = modifier,
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
            ) {
                PartialCircleBackground(
                    modifier = Modifier.fillMaxHeight()
                )

                Card(elevation = CardDefaults.cardElevation(),
                    colors = CardDefaults.cardColors(
                        containerColor = White,
                        contentColor = Color.Black
                    )
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp)
                    ) {
                        val (textTitle, textValue, dataField, indicatorBPM) = createRefs()

                        Text(
                            text = "Ваш Результат",
                            style = Typography.titleSmall,
                            modifier = Modifier.constrainAs(textTitle) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                        )

                        Text(
                            text = "Звичайно",
                            style = Typography.headlineMedium,
                            modifier = Modifier.constrainAs(textValue) {
                                top.linkTo(textTitle.bottom, margin = 8.dp)
                                start.linkTo(textTitle.start)
                            }
                        )

                        DateTimeDisplay(modifier = Modifier.constrainAs(dataField){
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        })

                        SegmentedValueIndicator(
                            value = 50f,
                            valueRange = 20f..140f,
                            breakpoints = listOf(60f, 100f),
                            listOf(
                                Color(0xFF4DD0E1),
                                Color(0xFF4CAF50),
                                Color(0xFFF44336)
                            ),
                            modifier = Modifier.constrainAs(indicatorBPM){
                                top.linkTo(textValue.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, name = "Result Screen Preview")
@Composable
private fun ResultScreenPreview() {
    TestTaskTheme {
        ResultScreen()
    }
}

@Composable
fun DateTimeDisplay(
    modifier: Modifier = Modifier,
    timestampMillis: Long = System.currentTimeMillis(),
    timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm"),
    dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    iconTint: Color = LocalContentColor.current.copy(alpha = 0.7f),
    textColor: Color = LocalContentColor.current.copy(alpha = 0.7f)
) {

    val dateTime = remember(timestampMillis) {
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestampMillis),
            ZoneId.systemDefault()
        )
    }

    val timeString = remember(dateTime, timeFormatter) {
        dateTime.format(timeFormatter)
    }
    val dateString = remember(dateTime, dateFormatter) {
        dateTime.format(dateFormatter)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.date_ocklock),
            contentDescription = "Час та дата",
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Column(
        ) {
            Text(
                text = timeString,
                style = Typography.bodyLarge,
                color = textColor
            )
            Text(
                text = dateString,
                style = Typography.bodyLarge,
                color = textColor
            )
        }
    }
}

@Composable
fun SegmentedValueIndicator(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    breakpoints: List<Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
    indicatorWidth: Dp = 4.dp,
    trackStrokeCap: StrokeCap = StrokeCap.Round,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round,
    indicatorColor: Color = Color.White
) {
    require(colors.size == breakpoints.size + 1) {
        "Number of colors must be one more than the number of breakpoints"
    }

    val trackHeightPx = with(LocalDensity.current) { trackHeight.toPx() }
    val indicatorWidthPx = with(LocalDensity.current) { indicatorWidth.toPx() }
    val halfIndicatorWidthPx = indicatorWidthPx / 2f

    val segments = remember(valueRange, breakpoints, colors) {
        buildList {
            var currentStart = valueRange.start

            breakpoints.forEachIndexed { index, breakpoint ->
                add(Triple(currentStart, breakpoint, colors[index]))
                currentStart = breakpoint
            }

            add(Triple(currentStart, valueRange.endInclusive, colors.last()))
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val yCenter = canvasHeight / 2f

        segments.forEach { (startValue, endValue, color) ->

            val startXRatio = ((startValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
            val endXRatio = ((endValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

            val startPx = startXRatio * canvasWidth
            val endPx = max(startPx + 1, endXRatio * canvasWidth)

            if (endPx > startPx) {
                drawLine(
                    color = color,
                    start = Offset(startPx, yCenter),
                    end = Offset(endPx, yCenter),
                    strokeWidth = trackHeightPx,
                    cap = trackStrokeCap
                )
            }
        }

        val valueRatio = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val indicatorXPx = valueRatio * canvasWidth

        drawLine(
            color = indicatorColor,
            start = Offset(indicatorXPx, yCenter - trackHeightPx / 2),
            end = Offset(indicatorXPx, yCenter + trackHeightPx / 2),
            strokeWidth = indicatorWidthPx,
            cap = indicatorStrokeCap
        )
    }
}
