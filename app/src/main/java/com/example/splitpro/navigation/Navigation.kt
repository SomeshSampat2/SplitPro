package com.example.splitpro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitpro.activity.ActivityScreen
import com.example.splitpro.auth.LoginScreen
import com.example.splitpro.groups.GroupsScreen
import com.example.splitpro.home.HomeScreen
import com.example.splitpro.profile.CreateProfileScreen
import com.example.splitpro.profile.ProfileScreen
import com.example.splitpro.splash.SplashScreen
import com.example.splitpro.ui.components.BottomNavItem

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object CreateProfile : Screen("create_profile")
    object Home : Screen("main_home")
}

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

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
            HomeScreen(navController)
        }
        
        composable(BottomNavItem.Groups.route) {
            GroupsScreen(navController)
        }
        composable(BottomNavItem.Activity.route) {
            ActivityScreen(navController)
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(navController)
        }
    }
}
