package com.assetmanager.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.assetmanager.app.ui.pages.assets.*
import com.assetmanager.app.ui.pages.home.HomeScreen
import com.assetmanager.app.ui.pages.settings.SettingsScreen
import com.assetmanager.app.ui.pages.statistics.StatisticsScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "首页", Icons.Default.Home)
    data object Assets : Screen("assets", "资产", Icons.Default.AccountBalanceWallet)
    data object Statistics : Screen("statistics", "统计", Icons.Default.BarChart)
    data object Settings : Screen("settings", "设置", Icons.Default.Person)
    data object AssetDetail : Screen("asset/{assetId}", "资产详情", Icons.Default.Info)
    data object AssetForm : Screen("asset/form?assetId={assetId}", "资产表单", Icons.Default.Edit)
    data object CategoryManage : Screen("category/manage", "分类管理", Icons.Default.Category)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Assets,
    Screen.Statistics,
    Screen.Settings
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddAsset = {
                        navController.navigate("asset/form?assetId=0")
                    },
                    onAssetClick = { assetId ->
                        navController.navigate("asset/$assetId")
                    }
                )
            }

            composable(Screen.Assets.route) {
                AssetListScreen(
                    onAddAsset = {
                        navController.navigate("asset/form?assetId=0")
                    },
                    onAssetClick = { assetId ->
                        navController.navigate("asset/$assetId")
                    },
                    onCategoryManage = {
                        navController.navigate(Screen.CategoryManage.route)
                    }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = "asset/{assetId}",
                arguments = listOf(
                    navArgument("assetId") { type = NavType.LongType }
                )
            ) {
                AssetDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onEdit = { assetId ->
                        navController.navigate("asset/form?assetId=$assetId")
                    }
                )
            }

            composable(
                route = "asset/form?assetId={assetId}",
                arguments = listOf(
                    navArgument("assetId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) {
                AssetFormScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CategoryManage.route) {
                CategoryManageScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
