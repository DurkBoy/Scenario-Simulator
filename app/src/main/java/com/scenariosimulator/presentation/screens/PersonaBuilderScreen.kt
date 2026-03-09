package com.scenariosimulator.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scenariosimulator.R
import com.scenariosimulator.presentation.components.PersonaCard
import com.scenariosimulator.presentation.persona.PersonaBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaBuilderScreen(
    navController: NavController,
    viewModel: PersonaBuilderViewModel = hiltViewModel()
) {
    val personas by viewModel.personas.collectAsState()
    val editorOpen by viewModel.editorOpen.collectAsState()
    val editingPersona by viewModel.isEditing.collectAsState()
    val name by viewModel.name.collectAsState()
    val role by viewModel.role.collectAsState()
    val expertise by viewModel.expertise.collectAsState()
    val tone by viewModel.tone.collectAsState()
    val worldview by viewModel.worldview.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val bias by viewModel.bias.collectAsState()
    val creativity by viewModel.creativityLevel.collectAsState()
    val skepticism by viewModel.skepticismLevel.collectAsState()
    val assertiveness by viewModel.assertivenessLevel.collectAsState()
    val collaboration by viewModel.collaborationLevel.collectAsState()
    val avatarColor by viewModel.avatarColor.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Persona Builder") },
                actions = {
                    IconButton(onClick = { viewModel.loadPresets() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_presets),
                            contentDescription = "Load Presets"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (editorOpen) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (editingPersona == null) "Create New Persona" else "Edit Persona",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = viewModel::updateName,
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = role,
                        onValueChange = viewModel::updateRole,
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = expertise,
                        onValueChange = viewModel::updateExpertise,
                        label = { Text("Expertise") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tone,
                        onValueChange = viewModel::updateTone,
                        label = { Text("Tone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = worldview,
                        onValueChange = viewModel::updateWorldview,
                        label = { Text("Worldview") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = goals,
                        onValueChange = viewModel::updateGoals,
                        label = { Text("Goals") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = bias,
                        onValueChange = viewModel::updateBias,
                        label = { Text("Bias") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Creativity: ${"%.2f".format(creativity)}")
                    Slider(value = creativity, onValueChange = viewModel::updateCreativityLevel)
                }
                item {
                    Text("Skepticism: ${"%.2f".format(skepticism)}")
                    Slider(value = skepticism, onValueChange = viewModel::updateSkepticismLevel)
                }
                item {
                    Text("Assertiveness: ${"%.2f".format(assertiveness)}")
                    Slider(value = assertiveness, onValueChange = viewModel::updateAssertivenessLevel)
                }
                item {
                    Text("Collaboration: ${"%.2f".format(collaboration)}")
                    Slider(value = collaboration, onValueChange = viewModel::updateCollaborationLevel)
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Avatar Color")
                        Spacer(modifier = Modifier.weight(1f))
                        listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta, Color.Cyan).forEach { color ->
                            IconButton(onClick = { viewModel.updateAvatarColor(color) }) {
                                Card(
                                    modifier = Modifier.size(32.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = color)
                                ) {
                                    if (avatarColor == color) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✓", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = viewModel::cancelEditing,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = viewModel::savePersona,
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotBlank() && role.isNotBlank()
                        ) {
                            Text("Save Persona")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { viewModel.startEditing() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create New Persona")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadPresets() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Load Preset Personas")
                        }
                    }
                }

                if (personas.isEmpty()) {
                    item {
                        Text(
                            text = "No personas yet. Create one or load presets to start building experiments.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    items(personas) { persona ->
                        PersonaCard(
                            persona = persona,
                            onClick = { viewModel.startEditing(persona) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
