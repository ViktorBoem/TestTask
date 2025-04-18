package com.example.testtask.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.unit.sp
import com.example.testtask.R
import com.example.testtask.ui.theme.Typography
import com.example.testtask.ui.theme.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun ResultDateTimeDisplay(
    modifier: Modifier = Modifier,
    timestampMillis: Long,
    iconTint: Color = DimGray,
    textColor: Color = DimGray,
    textSize: Dp = 16.dp,
    iconSize: Dp = 18.dp
) {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestampMillis),
        ZoneId.systemDefault()
    )

    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val formattedTime: String = dateTime.format(timeFormatter)
    val formattedDate: String = dateTime.format(dateFormatter)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.date_ocklock),
            contentDescription = "Час та дата",
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.width(textSize / 2))

        Column {
            Text(
                text = formattedTime,
                style = Typography.bodyLarge.copy(
                    color = textColor,
                    fontSize = textSize.value.sp
                )
            )

            Text(
                text = formattedDate,
                style = Typography.bodyLarge.copy(
                    color = textColor,
                    fontSize = textSize.value.sp
                ),
            )
        }
    }
}