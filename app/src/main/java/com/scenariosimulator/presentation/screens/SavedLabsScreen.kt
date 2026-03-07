package com.scenariosimulator.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun SavedLabsScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val experiments by viewModel.recentExperiments.collectAsState() // We'll use all experiments later

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Labs") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(experiments) { experiment ->
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
