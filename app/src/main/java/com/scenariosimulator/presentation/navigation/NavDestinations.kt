package com.scenariosimulator.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object CreateExperiment : Screen("create_experiment")
    object PersonaBuilder : Screen("persona_builder")
    object LiveSimulation : Screen("live_simulation/{experimentId}") {
        fun passId(experimentId: String) = "live_simulation/$experimentId"
    }
    object Summary : Screen("summary/{experimentId}") {
        fun passId(experimentId: String) = "summary/$experimentId"
    }
    object SavedLabs : Screen("saved_labs")
    object Settings : Screen("settings")
}
