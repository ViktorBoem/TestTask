package com.example.testtask.feature_onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.example.testtask.components.HeightSizedSquareWithRoundedBottom
import kotlinx.coroutines.launch
import com.example.testtask.feature_onboarding.component.*
import com.example.testtask.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit
) {
    val pageCount = onboardingPages.size
    val pagerState = rememberPagerState { pageCount }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier
            .weight(4f)
            .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HeightSizedSquareWithRoundedBottom(
                modifier = Modifier.fillMaxHeight()
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxHeight()
            ) { pageIndex ->
                OnboardingPage(pageData = onboardingPages[pageIndex])
            }
        }

        Column(modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pageCount - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                        else {
                            onNavigateToHome()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightRed
                )
            ) {
                Text(
                    if (pagerState.currentPage < pageCount - 1) "Продовжити" else "Почати!",
                    style = Typography.bodyLarge
                )
            }
        }
    }
}