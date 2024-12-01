package com.example.splitpro.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.splitpro.screens.activity.ActivityScreen
import com.example.splitpro.screens.groups.GroupsScreen
import com.example.splitpro.screens.home.HomeScreen
import com.example.splitpro.screens.profile.ProfileScreen
import com.example.splitpro.ui.theme.Primary

sealed class BottomNavItem(val route: String, val icon: @Composable () -> Unit, val label: String) {
    object Home : BottomNavItem("home", { Icon(Icons.Filled.Home, contentDescription = "Home") }, "Home")
    object Groups : BottomNavItem("groups", { Icon(Icons.Filled.AccountBox, contentDescription = "Groups") }, "Groups")
    object Activity : BottomNavItem("activity", { Icon(Icons.Filled.List, contentDescription = "Activity") }, "Activity")
    object Profile : BottomNavItem("profile", { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") }, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToCreateGroup: () -> Unit = {},
    onNavigateToGroupDetails: (String) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Groups,
        BottomNavItem.Activity,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = { BottomNav(navController = navController, items = items) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavigationGraph(
                navController = navController,
                onNavigateToCreateGroup = onNavigateToCreateGroup,
                onNavigateToGroupDetails = onNavigateToGroupDetails,
                onSignOut = onSignOut
            )
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToGroupDetails: (String) -> Unit,
    onSignOut: () -> Unit
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) { HomeScreen() }
        composable(BottomNavItem.Groups.route) { GroupsScreen(onAddGroup = onNavigateToCreateGroup, onGroupClick = onNavigateToGroupDetails) }
        composable(BottomNavItem.Activity.route) { ActivityScreen() }
        composable(BottomNavItem.Profile.route) { ProfileScreen(onSignOut = onSignOut) }
    }
}
