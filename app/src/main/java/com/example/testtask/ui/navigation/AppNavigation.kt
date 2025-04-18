package com.example.testtask.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testtask.ui.feature_home.HomeScreen
import com.example.testtask.ui.feature_loading.LoadingScreen
import com.example.testtask.ui.feature_onboarding.OnBoardingScreen
import com.example.testtask.ui.feature_pulse_measurement.PressureMeasurementScreen
import com.example.testtask.ui.feature_result.ResultScreen
import com.example.testtask.ui.navigation.navigation_data_object.NavigationDestinations
import com.example.testtask.ui.feature_history.HistoryScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavigationDestinations.LOADING_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = NavigationDestinations.LOADING_ROUTE) {
            LoadingScreen(
                onNavigateToOnboarding = {
                    navController.navigate(NavigationDestinations.ONBOARDING_ROUTE) {
                        popUpTo(NavigationDestinations.LOADING_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(route = NavigationDestinations.ONBOARDING_ROUTE) {
            OnBoardingScreen(onNavigateToHome = {
                navController.navigate(NavigationDestinations.HOME_ROUTE) {
                    popUpTo(NavigationDestinations.ONBOARDING_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }

        composable(route = NavigationDestinations.HOME_ROUTE) {
            HomeScreen(
                onNavigateToPressureMeasurement = {
                    navController.navigate(NavigationDestinations.PRESSURE_MEASUREMENT_ROUTE)
                },
                onNavigateToHistory = {
                    navController.navigate(NavigationDestinations.HISTORY_ROUTE)
                })
        }

        composable(route = NavigationDestinations.PRESSURE_MEASUREMENT_ROUTE) {
            PressureMeasurementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigationResult = {
                    navController.navigate(NavigationDestinations.RESULT_ROUTE) {
                        popUpTo(NavigationDestinations.PRESSURE_MEASUREMENT_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = NavigationDestinations.RESULT_ROUTE) {
            ResultScreen(
                onNavigateToHome = {
                    navController.navigate(NavigationDestinations.HOME_ROUTE) {
                        popUpTo(NavigationDestinations.HOME_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(NavigationDestinations.HISTORY_ROUTE) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = NavigationDestinations.HISTORY_ROUTE) {
            HistoryScreen(
                onNavigateToHome = {
                    navController.navigate(NavigationDestinations.HOME_ROUTE) {
                    }
                }
            )
        }
    }
}