package com.scenariosimulator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scenariosimulator.presentation.screens.CreateExperimentScreen
import com.scenariosimulator.presentation.screens.HomeScreen
import com.scenariosimulator.presentation.screens.LiveSimulationScreen
import com.scenariosimulator.presentation.screens.PersonaBuilderScreen
import com.scenariosimulator.presentation.screens.SavedLabsScreen
import com.scenariosimulator.presentation.screens.SettingsScreen
import com.scenariosimulator.presentation.screens.SplashScreen
import com.scenariosimulator.presentation.screens.SummaryScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.CreateExperiment.route) {
            CreateExperimentScreen(navController)
        }
        composable(Screen.PersonaBuilder.route) {
            PersonaBuilderScreen(navController)
        }
        composable(Screen.LiveSimulation.route) { backStackEntry ->
            LiveSimulationScreen(navController)
        }
        composable(Screen.Summary.route) { backStackEntry ->
            SummaryScreen(navController)
        }
        composable(Screen.SavedLabs.route) {
            SavedLabsScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
