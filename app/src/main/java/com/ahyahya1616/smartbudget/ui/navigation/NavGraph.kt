package com.ahyahya1616.smartbudget.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ahyahya1616.smartbudget.ui.screens.AddEditExpenseScreen
import com.ahyahya1616.smartbudget.ui.screens.ExpensesScreen
import com.ahyahya1616.smartbudget.ui.screens.SettingsScreen
import com.ahyahya1616.smartbudget.ui.screens.StatsScreen
import com.ahyahya1616.smartbudget.ui.viewmodel.BudgetViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Expenses : Screen("expenses", "Dépenses", Icons.Filled.List)
    object Stats : Screen("stats", "Stats", Icons.Filled.PieChart)
    object Settings : Screen("settings", "Paramètres", Icons.Filled.Settings)
    object AddEdit : Screen("add_edit", "Ajouter", Icons.Filled.Add)
}

val bottomNavItems = listOf(
    Screen.Expenses,
    Screen.Stats,
    Screen.Settings
)

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isBottomNavVisible = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (isBottomNavVisible) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
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
        },
        floatingActionButton = {
            if (currentRoute == Screen.Expenses.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddEdit.route) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Expense")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Expenses.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Expenses.route) {
                ExpensesScreen(viewModel)
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(Screen.AddEdit.route) {
                AddEditExpenseScreen(viewModel, onNavigateUp = { navController.navigateUp() })
            }
        }
    }
}
