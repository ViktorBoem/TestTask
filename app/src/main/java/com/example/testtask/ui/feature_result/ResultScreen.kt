package com.example.testtask.ui.feature_result

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.feature_result.result_component.*
import com.example.testtask.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = hiltViewModel(),
    onDoneClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { ResultTopAppBar() },
        modifier = modifier,
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text(
                        text = "Помилка: ${uiState.error}",
                        color = Red,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
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

                            Card(
                                elevation = CardDefaults.cardElevation(),
                                colors = CardDefaults.cardColors(
                                    containerColor = White,
                                    contentColor = Black
                                ),
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                ConstraintLayout(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
                                ) {
                                    val (textTitle, textValue, dataField, indicatorBPM,
                                        slowedStatusBPM, defaultStatusBPM, fastedStatusBPM,
                                        textRangeValueSlowedBPM, textRangeValueDefaultBPM, textRangeValueFastedBPM) = createRefs()

                                    val valueBPM = uiState.bpm

                                    Text(
                                        text = "Ваш Результат",
                                        style = Typography.titleMedium,
                                        modifier = Modifier.constrainAs(textTitle) {
                                            top.linkTo(parent.top)
                                            start.linkTo(parent.start)
                                        }
                                    )

                                    Text(
                                        text = when {
                                            valueBPM == 0 && uiState.error == null -> "Не визначено"
                                            valueBPM < 60 -> "Уповільнений"
                                            valueBPM in 60..100 -> "Звичайний"
                                            valueBPM > 100 -> "Прискорений"
                                            else -> "Н/Д"
                                        },
                                        style = Typography.headlineMedium,
                                        color = when {
                                            valueBPM == 0 && uiState.error == null -> DimGray
                                            valueBPM < 60 -> RobinEggBlue
                                            valueBPM in 60..100 -> Aquamarine
                                            valueBPM > 100 -> BitterSweet
                                            else -> Red
                                        },
                                        modifier = Modifier.constrainAs(textValue) {
                                            top.linkTo(textTitle.bottom, margin = 4.dp)
                                            start.linkTo(textTitle.start)
                                        }
                                    )

                                    ResultDateTimeDisplay(
                                        timeString = uiState.formattedTime,
                                        dateString = uiState.formattedDate,
                                        modifier = Modifier.constrainAs(dataField) {
                                            baseline.linkTo(textTitle.baseline)
                                            end.linkTo(parent.end)
                                        }
                                    )

                                    SegmentedValueIndicator(
                                        value = valueBPM,
                                        valueRange = 20..140,
                                        breakpoints = listOf(60, 100),
                                        colors = listOf(
                                            RobinEggBlue,
                                            Aquamarine,
                                            BitterSweet
                                        ),
                                        modifier = Modifier.constrainAs(indicatorBPM) {
                                            top.linkTo(textValue.bottom, margin = 16.dp)
                                            start.linkTo(parent.start)
                                            end.linkTo(parent.end)
                                        }
                                    )

                                    IndicatorStatusBPM(
                                        text = "Уповільнений",
                                        indicatorColor = RobinEggBlue,
                                        modifier = Modifier.constrainAs(slowedStatusBPM) {
                                            top.linkTo(indicatorBPM.bottom, margin = 32.dp)
                                            start.linkTo(parent.start)
                                        }
                                    )

                                    Text(
                                        text = "<60 BPM",
                                        style = Typography.bodySmall,
                                        color = if (valueBPM < 60) Black else DimGray,
                                        modifier = Modifier.constrainAs(textRangeValueSlowedBPM) {
                                            top.linkTo(slowedStatusBPM.top)
                                            bottom.linkTo(slowedStatusBPM.bottom)
                                            end.linkTo(parent.end)
                                        }
                                    )

                                    IndicatorStatusBPM(
                                        text = "Звичайний",
                                        indicatorColor = Aquamarine,
                                        modifier = Modifier.constrainAs(defaultStatusBPM){
                                            top.linkTo(slowedStatusBPM.bottom, margin = 12.dp)
                                            start.linkTo(parent.start)
                                        }
                                    )

                                    Text(
                                        text = "60-100 BPM",
                                        style = Typography.bodySmall,
                                        color = when{
                                            valueBPM in 60..100 -> Black
                                            else -> DimGray
                                        },
                                        modifier = Modifier.constrainAs(textRangeValueDefaultBPM) {
                                            top.linkTo(defaultStatusBPM.top)
                                            bottom.linkTo(defaultStatusBPM.bottom)
                                            end.linkTo(parent.end)
                                        }
                                    )

                                    IndicatorStatusBPM(
                                        text = "Пришвидшений",
                                        indicatorColor = BitterSweet,
                                        modifier = Modifier.constrainAs(fastedStatusBPM){
                                            top.linkTo(defaultStatusBPM.bottom, margin = 12.dp)
                                            start.linkTo(parent.start)
                                        }
                                    )

                                    Text(
                                        text = ">100 BPM",
                                        style = Typography.bodySmall,
                                        color = when{
                                            valueBPM > 100 -> Black
                                            else -> DimGray
                                        },
                                        modifier = Modifier.constrainAs(textRangeValueFastedBPM) {
                                            top.linkTo(fastedStatusBPM.top)
                                            bottom.linkTo(fastedStatusBPM.bottom)
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
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = onDoneClick,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightRed
                                )
                            ) {
                                Text(
                                    "Готово",
                                    style = Typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}