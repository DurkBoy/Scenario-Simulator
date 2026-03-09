package com.scenariosimulator.domain.engine

import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.ExperimentStatus
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.MessageType
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.model.SummaryResult
import com.scenariosimulator.domain.repository.ExperimentRepository
import com.scenariosimulator.domain.repository.PersonaRepository
import com.scenariosimulator.domain.service.AIServiceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimulationEngine @Inject constructor(
    private val aiServiceManager: AIServiceManager,
    private val experimentRepository: ExperimentRepository,
    private val personaRepository: PersonaRepository
) {

    data class SimulationState(
        val currentRound: Int = 1,
        val activePersonaId: String? = null,
        val isPaused: Boolean = false,
        val isComplete: Boolean = false,
        val consensusScore: Float = 0f,
        val tensionScore: Float = 0f,
        val messages: List<Message> = emptyList(),
        val experimentId: String? = null
    )

    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val turnMutex = Mutex()

    private val _simulationState = MutableStateFlow(SimulationState())
    val simulationState: StateFlow<SimulationState> = _simulationState.asStateFlow()

    private var currentExperiment: Experiment? = null
    private var currentPersonas: Map<String, Persona> = emptyMap()
    private var currentRound = 1
    private var currentPersonaIndex = 0
    private var isRunning = false
    private var activeTurnJob: Job? = null

    fun startSimulation(experiment: Experiment) {
        engineScope.launch {
            turnMutex.withLock {
                activeTurnJob?.cancelAndJoin()
                currentExperiment = experiment.copy(
                    status = ExperimentStatus.IN_PROGRESS,
                    transcript = emptyList()
                )
                currentRound = 1
                currentPersonaIndex = 0
                isRunning = true

                _simulationState.value = SimulationState(
                    currentRound = 1,
                    activePersonaId = null,
                    isPaused = false,
                    isComplete = false,
                    consensusScore = 0f,
                    tensionScore = 0f,
                    messages = emptyList(),
                    experimentId = experiment.id
                )

                currentPersonas = experiment.participantIds.mapNotNull { id ->
                    personaRepository.getPersonaByIdSync(id)
                }.associateBy { it.id }

                persistCurrentExperiment()
                scheduleNextTurnLocked()
            }
        }
    }

    fun pauseSimulation() {
        engineScope.launch {
            turnMutex.withLock {
                if (!isRunning || _simulationState.value.isComplete) return@withLock
                _simulationState.update { it.copy(isPaused = true, activePersonaId = null) }
                activeTurnJob?.cancelAndJoin()
                activeTurnJob = null
                persistCurrentExperiment()
            }
        }
    }

    fun resumeSimulation() {
        engineScope.launch {
            turnMutex.withLock {
                if (!isRunning || _simulationState.value.isComplete) return@withLock
                if (!_simulationState.value.isPaused) return@withLock
                _simulationState.update { it.copy(isPaused = false) }
                scheduleNextTurnLocked()
            }
        }
    }

    fun stopSimulation() {
        engineScope.launch {
            turnMutex.withLock {
                stopSimulationLocked(generateSummary = false)
            }
        }
    }

    fun injectPrompt(newPrompt: String) {
        val trimmed = newPrompt.trim()
        if (trimmed.isEmpty()) return

        engineScope.launch {
            turnMutex.withLock {
                val experiment = currentExperiment ?: return@withLock

                val systemMessage = Message(
                    experimentId = experiment.id,
                    personaId = "system",
                    content = "User steering directive: $trimmed",
                    timestamp = System.currentTimeMillis(),
                    turnNumber = _simulationState.value.messages.size + 1,
                    messageType = MessageType.SYSTEM
                )

                _simulationState.update { state ->
                    state.copy(messages = state.messages + systemMessage)
                }

                currentExperiment = experiment.copy(transcript = _simulationState.value.messages)
                persistCurrentExperiment()

                if (isRunning && !_simulationState.value.isPaused) {
                    activeTurnJob?.cancelAndJoin()
                    activeTurnJob = null
                    scheduleNextTurnLocked()
                }
            }
        }
    }

    fun forceNextSpeaker() {
        engineScope.launch {
            turnMutex.withLock {
                if (!isRunning || _simulationState.value.isComplete) return@withLock
                activeTurnJob?.cancelAndJoin()
                activeTurnJob = null
                advanceToNextSpeakerLocked()
                scheduleNextTurnLocked()
            }
        }
    }

    fun requestSummary() {
        engineScope.launch {
            turnMutex.withLock {
                val experiment = currentExperiment ?: return@withLock
                val history = _simulationState.value.messages
                val summaryResult = aiServiceManager.generateSummary(experiment, history)
                val summaryMessage = createSummaryMessage(experiment.id, summaryResult)

                _simulationState.update { state ->
                    state.copy(messages = state.messages + summaryMessage)
                }

                currentExperiment = experiment.copy(
                    transcript = _simulationState.value.messages,
                    summary = summaryResult
                )

                recalculateScoresLocked()
                persistCurrentExperiment()
            }
        }
    }

    private fun scheduleNextTurnLocked() {
        if (!isRunning) return
        if (_simulationState.value.isPaused || _simulationState.value.isComplete) return
        if (activeTurnJob?.isActive == true) return

        val experiment = currentExperiment ?: return
        if (currentPersonas.isEmpty() || experiment.participantIds.isEmpty()) return

        if (currentPersonaIndex >= experiment.participantIds.size) {
            currentPersonaIndex = 0
            currentRound += 1
        }

        if (currentRound > experiment.settings.defaultRounds) {
            activeTurnJob = engineScope.launch {
                turnMutex.withLock {
                    stopSimulationLocked(generateSummary = true)
                }
            }
            return
        }

        val personaId = experiment.participantIds.getOrNull(currentPersonaIndex) ?: return
        val persona = currentPersonas[personaId] ?: return

        _simulationState.update {
            it.copy(
                currentRound = currentRound,
                activePersonaId = personaId
            )
        }

        activeTurnJob = engineScope.launch {
            delay(experiment.settings.responseSpeed.toLong().coerceAtLeast(0L))

            val history = _simulationState.value.messages
            val response = aiServiceManager.generateResponse(
                experiment = currentExperiment ?: experiment,
                persona = persona,
                conversationHistory = history,
                userPrompt = experiment.prompt
            )

            turnMutex.withLock {
                if (!isRunning || _simulationState.value.isPaused || _simulationState.value.isComplete) {
                    activeTurnJob = null
                    return@withLock
                }

                val liveExperiment = currentExperiment ?: return@withLock
                val message = Message(
                    experimentId = liveExperiment.id,
                    personaId = persona.id,
                    content = response,
                    timestamp = System.currentTimeMillis(),
                    turnNumber = _simulationState.value.messages.size + 1,
                    messageType = MessageType.REGULAR
                )

                _simulationState.update { state ->
                    state.copy(
                        messages = state.messages + message,
                        activePersonaId = null
                    )
                }

                currentExperiment = liveExperiment.copy(
                    transcript = _simulationState.value.messages,
                    status = ExperimentStatus.IN_PROGRESS
                )

                recalculateScoresLocked()
                persistCurrentExperiment()

                advanceToNextSpeakerLocked()
                activeTurnJob = null
                scheduleNextTurnLocked()
            }
        }
    }

    private suspend fun stopSimulationLocked(generateSummary: Boolean) {
        activeTurnJob?.cancelAndJoin()
        activeTurnJob = null

        val experiment = currentExperiment
        if (experiment == null) {
            isRunning = false
            _simulationState.update { it.copy(isComplete = true, activePersonaId = null) }
            return
        }

        val summaryResult = if (generateSummary && _simulationState.value.messages.isNotEmpty()) {
            aiServiceManager.generateSummary(experiment, _simulationState.value.messages)
        } else {
            experiment.summary
        }

        val finalMessages = if (generateSummary && summaryResult != null &&
            _simulationState.value.messages.none { it.messageType == MessageType.SUMMARY }
        ) {
            _simulationState.value.messages + createSummaryMessage(experiment.id, summaryResult)
        } else {
            _simulationState.value.messages
        }

        currentExperiment = experiment.copy(
            status = ExperimentStatus.COMPLETED,
            transcript = finalMessages,
            summary = summaryResult
        )

        isRunning = false
        _simulationState.update {
            it.copy(
                isPaused = false,
                isComplete = true,
                activePersonaId = null,
                messages = finalMessages
            )
        }

        recalculateScoresLocked()
        persistCurrentExperiment()
    }

    private fun advanceToNextSpeakerLocked() {
        currentPersonaIndex += 1
    }

    private fun createSummaryMessage(experimentId: String, summary: SummaryResult): Message {
        val summaryText = buildString {
            appendLine("Key Takeaways:")
            summary.keyTakeaways.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Major Disagreements:")
            summary.majorDisagreements.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Strongest Ideas:")
            summary.strongestIdeas.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Action Plan:")
            summary.actionPlan.forEach { appendLine("- $it") }
            appendLine()
            appendLine("Consensus:")
            appendLine(summary.consensus)
            appendLine()
            appendLine("Open Questions:")
            summary.openQuestions.forEach { appendLine("- $it") }
        }.trim()

        return Message(
            experimentId = experimentId,
            personaId = "summary",
            content = summaryText,
            timestamp = System.currentTimeMillis(),
            turnNumber = _simulationState.value.messages.size + 1,
            messageType = MessageType.SUMMARY
        )
    }

    private fun recalculateScoresLocked() {
        val regularMessages = _simulationState.value.messages.filter { it.messageType == MessageType.REGULAR }
        if (regularMessages.isEmpty()) {
            _simulationState.update { it.copy(consensusScore = 0f, tensionScore = 0f) }
            return
        }

        val disagreementHits = regularMessages.count {
            val c = it.content.lowercase()
            c.contains("disagree") || c.contains("however") || c.contains("but ") || c.contains("concern")
        }
        val agreementHits = regularMessages.count {
            val c = it.content.lowercase()
            c.contains("agree") || c.contains("build on") || c.contains("support") || c.contains("aligned")
        }

        val total = regularMessages.size.toFloat()
        val tension = (disagreementHits / total).coerceIn(0f, 1f)
        val consensus = (agreementHits / total).coerceIn(0f, 1f)

        _simulationState.update {
            it.copy(
                tensionScore = tension,
                consensusScore = consensus
            )
        }
    }

    private fun persistCurrentExperiment() {
        val experiment = currentExperiment ?: return
        engineScope.launch {
            experimentRepository.saveExperiment(experiment)
        }
    }
}
