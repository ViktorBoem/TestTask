package com.example.testtask.ui.feature_history.history_component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

fun Modifier.lazyListVerticalScrollbar(
    state: LazyListState,
    scrollBarWidth: Dp = 4.dp,
    minScrollBarHeightRatio: Float = 0.1f,
    scrollBarColor: Color = Color.Blue,
    cornerRadius: Dp = 2.dp
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "ScrollbarAlpha"
    )

    drawWithContent {
        drawContent()

        val firstVisibleItemIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f
        if (needDrawScrollbar && state.layoutInfo.totalItemsCount > state.layoutInfo.visibleItemsInfo.size) {

            val viewportHeight = this.size.height
            val visibleItemsRatio = state.layoutInfo.visibleItemsInfo.size.toFloat() / state.layoutInfo.totalItemsCount
            val scrollBarHeight =
                max(viewportHeight * visibleItemsRatio, viewportHeight * minScrollBarHeightRatio)

            val scrollOffset = state.firstVisibleItemIndex.toFloat() / (state.layoutInfo.totalItemsCount - state.layoutInfo.visibleItemsInfo.size).toFloat()
            val limitedScrollOffset = scrollOffset.coerceIn(0f, 1f)

            val scrollBarOffsetY = limitedScrollOffset * (viewportHeight - scrollBarHeight)

            drawRoundRect(
                color = scrollBarColor,
                topLeft = Offset(this.size.width - scrollBarWidth.toPx(), scrollBarOffsetY),
                size = Size(scrollBarWidth.toPx(), scrollBarHeight),
                alpha = alpha,
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    }
}