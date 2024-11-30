package com.example.splitpro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.splitpro.auth.LoginScreen
import com.example.splitpro.CreateProfile.CreateProfileScreen
import com.example.splitpro.splash.SplashScreen
import com.example.splitpro.screens.MainScreen
import com.example.splitpro.screens.CreateGroupScreen
import com.example.splitpro.screens.GroupDetailsScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CREATE_PROFILE = "create_profile"
    const val MAIN = "main"
    const val CREATE_GROUP = "create_group"
    const val GROUP_DETAILS = "group_details/{groupId}"

    fun groupDetails(groupId: String) = "group_details/$groupId"
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
                },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
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
            MainScreen(
                onNavigateToCreateGroup = {
                    navController.navigate(Routes.CREATE_GROUP)
                },
                onNavigateToGroupDetails = { groupId ->
                    navController.navigate(Routes.groupDetails(groupId))
                },
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.GROUP_DETAILS,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) {
            GroupDetailsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
