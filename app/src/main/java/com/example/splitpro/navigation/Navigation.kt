package com.example.splitpro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.splitpro.auth.LoginScreen
import com.example.splitpro.home.HomeScreen
import com.example.splitpro.profile.CreateProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CreateProfile : Screen("create_profile")
    object Home : Screen("home")
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onSignInClick = { /* Will be handled by AuthViewModel */ },
                onNavigateToProfile = {
                    navController.navigate(Screen.CreateProfile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CreateProfile.route) {
            CreateProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CreateProfile.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
