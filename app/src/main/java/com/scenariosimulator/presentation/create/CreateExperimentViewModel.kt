package com.scenariosimulator.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.ExperimentMode
import com.scenariosimulator.domain.model.ExperimentSettings
import com.scenariosimulator.domain.model.ExperimentStatus
import com.scenariosimulator.domain.model.ModelSource
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.repository.ExperimentRepository
import com.scenariosimulator.domain.repository.PersonaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateExperimentViewModel @Inject constructor(
    private val experimentRepository: ExperimentRepository,
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedMode = MutableStateFlow(ExperimentMode.DEBATE)
    val selectedMode: StateFlow<ExperimentMode> = _selectedMode.asStateFlow()

    private val _selectedPersonas = MutableStateFlow<List<Persona>>(emptyList())
    val selectedPersonas: StateFlow<List<Persona>> = _selectedPersonas.asStateFlow()

    private val _availablePersonas = MutableStateFlow<List<Persona>>(emptyList())
    val availablePersonas: StateFlow<List<Persona>> = _availablePersonas.asStateFlow()

    private val _selectedModelSource = MutableStateFlow(ModelSource.GEMINI)
    val selectedModelSource: StateFlow<ModelSource> = _selectedModelSource.asStateFlow()

    private val _rounds = MutableStateFlow(5)
    val rounds: StateFlow<Int> = _rounds.asStateFlow()

    private val _responseLength = MutableStateFlow(150)
    val responseLength: StateFlow<Int> = _responseLength.asStateFlow()

    init {
        loadPersonas()
    }

    private fun loadPersonas() {
        viewModelScope.launch {
            personaRepository.getAllPersonas().collect { personas ->
                _availablePersonas.value = personas
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updatePrompt(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun updateMode(mode: ExperimentMode) {
        _selectedMode.value = mode
    }

    fun togglePersona(persona: Persona) {
        val current = _selectedPersonas.value.toMutableList()
        if (current.contains(persona)) {
            current.remove(persona)
        } else {
            if (current.size < 8) {
                current.add(persona)
            }
        }
        _selectedPersonas.value = current
    }

    fun updateModelSource(source: ModelSource) {
        _selectedModelSource.value = source
    }

    fun updateRounds(rounds: Int) {
        _rounds.value = rounds
    }

    fun updateResponseLength(length: Int) {
        _responseLength.value = length
    }

    fun createExperiment(): String {
        val experiment = Experiment(
            title = _title.value,
            prompt = _prompt.value,
            mode = _selectedMode.value,
            participantIds = _selectedPersonas.value.map { it.id },
            settings = ExperimentSettings(
                defaultRounds = _rounds.value,
                defaultResponseLength = _responseLength.value,
                modelSource = _selectedModelSource.value
            ),
            status = ExperimentStatus.CREATED
        )
        viewModelScope.launch {
            experimentRepository.saveExperiment(experiment)
        }
        return experiment.id
    }
}
