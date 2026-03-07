package com.scenariosimulator.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scenariosimulator.domain.model.ExperimentMode
import com.scenariosimulator.domain.model.ModelSource
import com.scenariosimulator.presentation.components.ModeSelectorChip
import com.scenariosimulator.presentation.components.PersonaCard
import com.scenariosimulator.presentation.create.CreateExperimentViewModel
import com.scenariosimulator.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExperimentScreen(
    navController: NavController,
    viewModel: CreateExperimentViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val prompt by viewModel.prompt.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val selectedPersonas by viewModel.selectedPersonas.collectAsState()
    val availablePersonas by viewModel.availablePersonas.collectAsState()
    val selectedModelSource by viewModel.selectedModelSource.collectAsState()
    val rounds by viewModel.rounds.collectAsState()
    val responseLength by viewModel.responseLength.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Experiment") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = viewModel::updatePrompt,
                    label = { Text("Prompt") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            item {
                Text("Mode", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExperimentMode.values().forEach { mode ->
                        ModeSelectorChip(
                            label = mode.name,
                            isSelected = selectedMode == mode,
                            onClick = { viewModel.updateMode(mode) }
                        )
                    }
                }
            }
            item {
                Text("Select Personas (2-8)", style = MaterialTheme.typography.titleMedium)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availablePersonas) { persona ->
                        PersonaCard(
                            persona = persona,
                            isSelected = selectedPersonas.contains(persona),
                            onClick = { viewModel.togglePersona(persona) }
                        )
                    }
                }
            }
            item {
                Text("Model Source", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModelSource.values().forEach { source ->
                        ModeSelectorChip(
                            label = source.name,
                            isSelected = selectedModelSource == source,
                            onClick = { viewModel.updateModelSource(source) }
                        )
                    }
                }
            }
            item {
                Text("Rounds: $rounds", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = rounds.toFloat(),
                    onValueChange = { viewModel.updateRounds(it.toInt()) },
                    valueRange = 1f..20f,
                    steps = 19
                )
            }
            item {
                Text("Response Length: $responseLength", style = MaterialTheme.typography.titleMedium)
                Slider(
                    value = responseLength.toFloat(),
                    onValueChange = { viewModel.updateResponseLength(it.toInt()) },
                    valueRange = 50f..500f,
                    steps = 9
                )
            }
            item {
                Button(
                    onClick = {
                        val experimentId = viewModel.createExperiment()
                        navController.navigate(Screen.LiveSimulation.passId(experimentId))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank() && prompt.isNotBlank() && selectedPersonas.size in 2..8
                ) {
                    Text("Start Simulation")
                }
            }
        }
    }
}
