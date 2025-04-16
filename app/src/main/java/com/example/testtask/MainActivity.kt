package com.example.testtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.example.testtask.ui.theme.TestTaskTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testtask.ui.feature_home.HomeScreen
import com.example.testtask.ui.feature_loading.LoadingScreen
import com.example.testtask.ui.feature_onboarding.OnBoardingScreen
import com.example.testtask.ui.feature_pulse_measurement.PressureMeasurementScreen
import com.example.testtask.ui.feature_result.result_component.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TestTaskTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "loading",
                    modifier = Modifier.fillMaxSize())
                {
                    composable(route = "loading") {
                        LoadingScreen(
                            onNavigateToOnboarding = {
                                navController.navigate("onboarding") {
                                    popUpTo("loading") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(route = "onboarding") {
                        OnBoardingScreen(onNavigateToHome = {
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
                    }

                    composable(route = "home") {
                        HomeScreen(onNavigateToPressureMeasurement = {
                            navController.navigate("pressureMeasurement") { }
                        })
                    }

                    composable(route = "pressureMeasurement") {
                        PressureMeasurementScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },

                            onNavigationResult = {
                                navController.navigate("result")
                            })
                    }

                    composable(route = "result") {
                        ResultScreen()
                    }
                }
            }
        }
    }
}
