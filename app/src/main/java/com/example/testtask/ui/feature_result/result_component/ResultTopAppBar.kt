package com.example.testtask.ui.feature_result.result_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.testtask.ui.theme.*
import com.example.testtask.R
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTopAppBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text("Результат")},
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Історія",
                    style = Typography.titleLarge,
                    color = White
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.time_machine),
                    contentDescription = "іконка історії",
                    modifier = Modifier.fillMaxHeight(0.6f),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(4.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = LightRed,
            titleContentColor = LightRed,
            actionIconContentColor = LightRed
        ),
        modifier = modifier
    )
}