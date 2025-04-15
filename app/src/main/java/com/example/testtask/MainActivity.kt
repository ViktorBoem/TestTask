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
import com.example.testtask.feature_home.HomeScreen
import com.example.testtask.feature_loading.LoadingScreen
import com.example.testtask.feature_onboarding.OnBoardingScreen

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
                            onNavigateToHome = {
                                navController.navigate("onboarding") {
                                    popUpTo("loading") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(route = "home") {
                        HomeScreen()
                    }

                    composable(route = "onboarding") {
                        OnBoardingScreen(onNavigateToHome = {
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}
