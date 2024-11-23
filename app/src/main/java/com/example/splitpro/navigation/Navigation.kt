package com.example.splitpro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.splitpro.auth.LoginScreen
import com.example.splitpro.profile.CreateProfileScreen
import com.example.splitpro.splash.SplashScreen
import com.example.splitpro.screens.MainScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CREATE_PROFILE = "create_profile"
    const val MAIN = "main"
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onSignInClick = { /* Will be handled by AuthViewModel */ },
                onNavigateToProfile = {
                    navController.navigate(Routes.CREATE_PROFILE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.CREATE_PROFILE) {
            CreateProfileScreen(
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.CREATE_PROFILE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}
