package com.example.testtask.ui.feature_result.result_component

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
import androidx.compose.ui.unit.dp
import com.example.testtask.R
import com.example.testtask.ui.theme.Typography
import com.example.testtask.ui.theme.*

@Composable
fun ResultDateTimeDisplay(
    modifier: Modifier = Modifier,
    timeString: String,
    dateString: String,
    iconTint: Color = DimGray,
    textColor: Color = DimGray
) {
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
        Column {
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