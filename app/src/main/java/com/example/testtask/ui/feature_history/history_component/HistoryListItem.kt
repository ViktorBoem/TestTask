package com.example.testtask.ui.feature_history.history_component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.testtask.ui.components.ResultDateTimeDisplay
import com.example.testtask.ui.theme.Black
import com.example.testtask.ui.theme.Typography
import com.example.testtask.ui.theme.White

@Composable
fun HistoryListItem(
    modifier: Modifier = Modifier,
    valueBPM: Int,
    timestampMillis: Long,
    cornerRadius: Dp = 12.dp
){
    Row(
        modifier = modifier
            .background(
                color = White,
                shape = RoundedCornerShape(cornerRadius)
            )
            .fillMaxWidth(0.9f)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$valueBPM BPM",
            style = Typography.displaySmall,
            color = Black
        )

        Spacer(modifier = Modifier.weight(2f))

        VerticalDivider( )

        Spacer(modifier = Modifier.weight(1f))

        ResultDateTimeDisplay(
            timestampMillis = timestampMillis,
            iconSize = 0.dp,
            textSize = 24.dp
        )
    }
}