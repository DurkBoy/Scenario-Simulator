package com.scenariosimulator.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scenariosimulator.domain.model.ModelSource
import com.scenariosimulator.presentation.components.SettingsRow
import com.scenariosimulator.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkMode by viewModel.darkMode.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()
    val animationIntensity by viewModel.animationIntensity.collectAsState()
    val textSizeScale by viewModel.textSizeScale.collectAsState()
    val defaultRounds by viewModel.defaultRounds.collectAsState()
    val defaultResponseLength by viewModel.defaultResponseLength.collectAsState()
    val defaultCreativity by viewModel.defaultCreativity.collectAsState()
    val strictPersonaMode by viewModel.strictPersonaMode.collectAsState()
    val debateIntensity by viewModel.debateIntensity.collectAsState()
    val randomness by viewModel.randomness.collectAsState()
    val responseSpeed by viewModel.responseSpeed.collectAsState()
    val modelSource by viewModel.modelSource.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()
    val importStatus by viewModel.importStatus.collectAsState()

    val context = LocalContext.current
    var exportType by remember { mutableStateOf<SettingsViewModel.ExportType?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            exportType?.let { type ->
                viewModel.exportData(it, type)
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            exportType?.let { type ->
                viewModel.importData(it, type)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance
            Text("Appearance", style = MaterialTheme.typography.titleLarge)
            SettingsRow(
                title = "Dark Mode",
                checked = darkMode,
                onCheckedChange = viewModel::updateDarkMode
            )
            Text("Accent Color (hex): $accentColor")
            // In a real app, you'd have a proper color picker.

            Text("Animation Intensity: ${"%.2f".format(animationIntensity)}")
            Slider(value = animationIntensity, onValueChange = viewModel::updateAnimationIntensity)

            Text("Text Size Scale: ${"%.2f".format(textSizeScale)}")
            Slider(value = textSizeScale, onValueChange = viewModel::updateTextSizeScale)

            Spacer(modifier = Modifier.height(16.dp))

            // Experiment Defaults
            Text("Experiment Defaults", style = MaterialTheme.typography.titleLarge)
            Text("Default Rounds: $defaultRounds")
            Slider(value = defaultRounds.toFloat(), onValueChange = { viewModel.updateDefaultRounds(it.toInt()) }, valueRange = 1f..20f)
            Text("Default Response Length: $defaultResponseLength")
            Slider(value = defaultResponseLength.toFloat(), onValueChange = { viewModel.updateDefaultResponseLength(it.toInt()) }, valueRange = 50f..500f)
            Text("Default Creativity: ${"%.2f".format(defaultCreativity)}")
            Slider(value = defaultCreativity, onValueChange = viewModel::updateDefaultCreativity)

            Spacer(modifier = Modifier.height(16.dp))

            // AI Behavior
            Text("AI Behavior", style = MaterialTheme.typography.titleLarge)
            SettingsRow(
                title = "Strict Persona Mode",
                checked = strictPersonaMode,
                onCheckedChange = viewModel::updateStrictPersonaMode
            )
            Text("Debate Intensity: ${"%.2f".format(debateIntensity)}")
            Slider(value = debateIntensity, onValueChange = viewModel::updateDebateIntensity)
            Text("Randomness: ${"%.2f".format(randomness)}")
            Slider(value = randomness, onValueChange = viewModel::updateRandomness)
            Text("Response Speed (ms): $responseSpeed")
            Slider(value = responseSpeed.toFloat(), onValueChange = { viewModel.updateResponseSpeed(it.toInt()) }, valueRange = 100f..5000f)

            // Model Source
            Text("Model Source", style = MaterialTheme.typography.titleMedium)
            ModelSource.values().forEach { source ->
                SettingsRow(
                    title = source.name,
                    checked = modelSource == source.name,
                    onCheckedChange = { if (it) viewModel.updateModelSource(source.name) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notifications
            Text("Notifications", style = MaterialTheme.typography.titleLarge)
            SettingsRow(
                title = "Enable Notifications",
                checked = notificationsEnabled,
                onCheckedChange = viewModel::updateNotificationsEnabled
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Storage
            Text("Storage", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { viewModel.clearCache() }, modifier = Modifier.fillMaxWidth()) {
                Text("Clear Cache")
            }

            // Export buttons
            Text("Export", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                exportType = SettingsViewModel.ExportType.EXPERIMENTS
                exportLauncher.launch("experiments.json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Export Experiments")
            }
            Button(onClick = {
                exportType = SettingsViewModel.ExportType.PERSONAS
                exportLauncher.launch("personas.json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Export Personas")
            }
            Button(onClick = {
                exportType = SettingsViewModel.ExportType.ALL
                exportLauncher.launch("scenario_simulator_backup.json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Export All")
            }

            // Import buttons
            Text("Import", style = MaterialTheme.typography.titleMedium)
            Button(onClick = {
                exportType = SettingsViewModel.ExportType.EXPERIMENTS
                importLauncher.launch("application/json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Import Experiments")
            }
            Button(onClick = {
                exportType = SettingsViewModel.ExportType.PERSONAS
                importLauncher.launch("application/json")
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Import Personas")
            }

            if (exportStatus != null) {
                Text(exportStatus!!, color = MaterialTheme.colorScheme.primary)
            }
            if (importStatus != null) {
                Text(importStatus!!, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About
            Text("About", style = MaterialTheme.typography.titleLarge)
            Text("Version 1.0")
            Text("Privacy Policy")
            Text("Terms")
            Text("Credits")
        }
    }
}
