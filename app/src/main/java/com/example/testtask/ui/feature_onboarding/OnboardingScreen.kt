package com.example.testtask.ui.feature_onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.rememberCoroutineScope
import com.example.testtask.ui.components.PartialCircleBackground
import com.example.testtask.ui.components.SuperStylePrimaryActionButton
import kotlinx.coroutines.launch
import com.example.testtask.ui.feature_onboarding.onboarding_component.OnboardingPage
import com.example.testtask.ui.feature_onboarding.onboarding_component.PagerIndicator
import com.example.testtask.ui.feature_onboarding.onboarding_data_object.*

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
            PartialCircleBackground(
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.weight(1f)
            )

            SuperStylePrimaryActionButton(
                text = if (pagerState.currentPage < pageCount - 1) "Продовжити" else "Почати!",
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pageCount - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onNavigateToHome()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1.5f, fill = false)
                    .fillMaxWidth(0.9f)
            )
        }
    }
}