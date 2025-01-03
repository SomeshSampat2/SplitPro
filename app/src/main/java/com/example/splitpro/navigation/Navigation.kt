package com.example.splitpro.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.splitpro.auth.LoginScreen
import com.example.splitpro.screens.createProfile.CreateProfileScreen
import com.example.splitpro.splash.SplashScreen
import com.example.splitpro.screens.MainScreen
import com.example.splitpro.screens.groups.CreateGroupScreen
import com.example.splitpro.screens.groups.GroupDetailsScreen
import com.example.splitpro.screens.groups.AddGroupMemberScreen
import com.example.splitpro.screens.groups.GroupDetailsViewModel
import com.example.splitpro.screens.groups.AddExpenseScreen
import com.example.splitpro.screens.groups.SettleUpScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val CREATE_PROFILE = "create_profile"
    const val MAIN = "main"
    const val CREATE_GROUP = "create_group"
    const val GROUP_DETAILS = "group_details/{groupId}"
    const val ADD_GROUP_MEMBER = "add_group_member/{groupId}"
    const val ADD_EXPENSE = "add_expense/{groupId}"
    const val SETTLE_UP = "settle_up/{expenseId}"

    fun groupDetails(groupId: String) = "group_details/$groupId"
    fun addGroupMember(groupId: String) = "add_group_member/$groupId"
    fun addExpense(groupId: String) = "add_expense/$groupId"
    fun settleUp(expenseId: String) = "settle_up/$expenseId"
}

object Screen {
    object CreateGroup {
        const val route = "create_group"
    }

    object GroupDetails {
        const val route = "group_details"
        fun createRoute(groupId: String) = "$route/$groupId"
    }

    object AddGroupMember {
        const val route = "add_group_member"
        fun createRoute(groupId: String) = "$route/$groupId"
    }

    object AddExpense {
        const val route = "add_expense"
        fun createRoute(groupId: String) = "$route/$groupId"
    }

    object Groups {
        const val route = "groups"
    }

    object SettleUp {
        const val route = "settle_up"
        fun createRoute(expenseId: String) = "$route/$expenseId"
    }
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

        composable(route = Screen.CreateGroup.route) {
             CreateGroupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGroupDetails = { groupId ->
                    navController.navigate(Screen.GroupDetails.createRoute(groupId)) {
                        popUpTo(Screen.Groups.route)
                    }
                }
            )
        }

        composable(
            route = Routes.GROUP_DETAILS,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            GroupDetailsScreen(
                groupId = groupId,
                onNavigateBack = { navController.popBackStack() },
                onAddMember = { navController.navigate(Routes.addGroupMember(groupId)) },
                onAddExpense = { navController.navigate(Routes.addExpense(groupId)) },
                onSettleUpExpense = { expenseId -> navController.navigate(Routes.settleUp(expenseId)) }
            )
        }

        composable(
            route = Routes.ADD_GROUP_MEMBER,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            val viewModel: GroupDetailsViewModel = viewModel(
                viewModelStoreOwner = navController.getBackStackEntry(Routes.GROUP_DETAILS.replace("{groupId}", groupId))
            )
            AddGroupMemberScreen(
                groupId = groupId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.ADD_EXPENSE,
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
            AddExpenseScreen(
                groupId = groupId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.SETTLE_UP,
            arguments = listOf(
                navArgument("expenseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val expenseId = backStackEntry.arguments?.getString("expenseId") ?: ""
            SettleUpScreen(
                expenseId = expenseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
