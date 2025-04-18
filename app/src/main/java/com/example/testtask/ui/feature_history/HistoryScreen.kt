package com.example.testtask.ui.feature_history

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.theme.*
import com.example.testtask.ui.components.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testtask.ui.feature_history.history_component.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    onNavigateToHome : () -> Unit
) {
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val records = uiState.records

    Scaffold(
        topBar = { HistoryTopAppBar(onNavigateBack = onNavigateToHome) },
        modifier = modifier,
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PartialCircleBackground(
                modifier = Modifier.fillMaxHeight(0.8f)
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else if (records.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Історія записів порожня",
                        style = Typography.titleLarge,
                        color = Color.Gray
                    )
                }
            }
            else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp)
                        .lazyListVerticalScrollbar(
                            state = listState,
                            scrollBarWidth = 6.dp,
                            scrollBarColor = Color.Gray,
                            cornerRadius = 3.dp
                        ),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(items = records, key = { record -> record.id }) { record ->
                        HistoryListItem(
                            valueBPM = record.bpm,
                            timestampMillis = record.timestamp
                        )
                    }
                }
            }

            val showButton by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    val totalItemsCount = layoutInfo.totalItemsCount

                    totalItemsCount > 0 && lastVisibleItemIndex != null && lastVisibleItemIndex == totalItemsCount - 1
                }
            }

            AnimatedVisibility(
                visible = showButton,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(0.9f),
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                SuperStylePrimaryActionButton(
                    text = "Очистити Історію",
                    onClick = { viewModel.clearHistory() },
                    modifier = Modifier
                )
            }
        }
    }
}