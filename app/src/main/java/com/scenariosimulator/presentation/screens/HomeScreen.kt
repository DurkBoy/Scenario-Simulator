package com.scenariosimulator.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scenariosimulator.presentation.components.ExperimentCard
import com.scenariosimulator.presentation.home.HomeViewModel
import com.scenariosimulator.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val recentExperiments by viewModel.recentExperiments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scenario Simulator") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                Button(
                    onClick = { navController.navigate(Screen.CreateExperiment.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simulation Lab")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate(Screen.PersonaBuilder.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Persona Builder")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate(Screen.SavedLabs.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Saved Labs")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Settings")
                }
            }

            item {
                Text(
                    text = "Recent Labs",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (isLoading) {
                item {
                    Text("Loading recent labs...")
                }
            } else if (recentExperiments.isEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "No experiments yet. Start your first simulation or build some personas first.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(recentExperiments) { experiment ->
                    ExperimentCard(
                        experiment = experiment,
                        onClick = {
                            if (experiment.summary != null) {
                                navController.navigate(Screen.Summary.passId(experiment.id))
                            } else {
                                navController.navigate(Screen.LiveSimulation.passId(experiment.id))
                            }
                        }
                    )
                }
            }
        }
    }
}
