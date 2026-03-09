package com.scenariosimulator.presentation.live

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.engine.SimulationEngine
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.ExperimentStatus
import com.scenariosimulator.domain.model.Message
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
class LiveSimulationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val simulationEngine: SimulationEngine,
    private val experimentRepository: ExperimentRepository,
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val experimentId: String = savedStateHandle["experimentId"] ?: ""

    private val _experiment = MutableStateFlow<Experiment?>(null)
    val experiment: StateFlow<Experiment?> = _experiment.asStateFlow()

    private val _personaMap = MutableStateFlow<Map<String, Persona>>(emptyMap())
    val personaMap: StateFlow<Map<String, Persona>> = _personaMap.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _activePersonaId = MutableStateFlow<String?>(null)
    val activePersonaId: StateFlow<String?> = _activePersonaId.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete.asStateFlow()

    private val _consensusScore = MutableStateFlow(0f)
    val consensusScore: StateFlow<Float> = _consensusScore.asStateFlow()

    private val _tensionScore = MutableStateFlow(0f)
    val tensionScore: StateFlow<Float> = _tensionScore.asStateFlow()

    private val _injectPromptText = MutableStateFlow("")
    val injectPromptText: StateFlow<String> = _injectPromptText.asStateFlow()

    private var hasStartedSimulation = false

    init {
        loadExperiment()
        observeSimulationState()
    }

    private fun loadExperiment() {
        viewModelScope.launch {
            experimentRepository.getExperiment(experimentId).collect { experiment ->
                _experiment.value = experiment
                experiment?.let {
                    val personas = it.participantIds.mapNotNull { id ->
                        personaRepository.getPersonaByIdSync(id)
                    }.associateBy { persona -> persona.id }
                    _personaMap.value = personas
                    _messages.value = it.transcript
                    _isPaused.value = it.status == ExperimentStatus.PAUSED
                    _isComplete.value = it.status == ExperimentStatus.COMPLETED
                }
            }
        }
    }

    private fun observeSimulationState() {
        viewModelScope.launch {
            simulationEngine.simulationState.collect { state ->
                if (state.experimentId != experimentId) return@collect
                _messages.value = state.messages
                _activePersonaId.value = state.activePersonaId
                _isPaused.value = state.isPaused
                _isComplete.value = state.isComplete
                _consensusScore.value = state.consensusScore
                _tensionScore.value = state.tensionScore
            }
        }
    }

    fun ensureSimulationStarted() {
        val current = _experiment.value ?: return
        if (hasStartedSimulation) return
        if (current.participantIds.isEmpty()) return
        if (current.status != ExperimentStatus.CREATED) return

        hasStartedSimulation = true
        simulationEngine.startSimulation(current)
    }

    fun pauseSimulation() {
        simulationEngine.pauseSimulation()
    }

    fun resumeSimulation() {
        simulationEngine.resumeSimulation()
    }

    fun stopSimulation() {
        simulationEngine.stopSimulation()
    }

    fun updateInjectPrompt(value: String) {
        _injectPromptText.value = value
    }

    fun injectPrompt() {
        val prompt = _injectPromptText.value.trim()
        if (prompt.isEmpty()) return
        simulationEngine.injectPrompt(prompt)
        _injectPromptText.value = ""
    }

    fun forceNextSpeaker() {
        simulationEngine.forceNextSpeaker()
    }

    fun requestSummary() {
        simulationEngine.requestSummary()
    }
}
