package com.scenariosimulator.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scenariosimulator.R
import com.scenariosimulator.presentation.components.AnimatedCTAButton
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
                title = { Text("Scenario Simulator") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(painterResource(id = R.drawable.ic_settings), contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AnimatedCTAButton(
                text = "Start New Experiment",
                onClick = {
                    navController.navigate(Screen.CreateExperiment.route)
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Recent Experiments",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Text("Loading...")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentExperiments) { experiment ->
                        ExperimentCard(
                            experiment = experiment,
                            onClick = {
                                navController.navigate(Screen.LiveSimulation.passId(experiment.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
