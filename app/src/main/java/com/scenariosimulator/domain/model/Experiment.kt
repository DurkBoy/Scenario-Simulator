package com.scenariosimulator.domain.model

import java.util.UUID

data class Experiment(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val prompt: String,
    val mode: ExperimentMode,
    val participantIds: List<String>, // IDs of personas
    val settings: ExperimentSettings,
    val createdAt: Long = System.currentTimeMillis(),
    val status: ExperimentStatus,
    val transcript: List<Message> = emptyList(),
    val summary: SummaryResult? = null
)

enum class ExperimentMode {
    DEBATE,
    BRAINSTORM,
    COLLABORATION,
    ADVERSARIAL,
    MODERATOR_LED,
    CHAOS
}

enum class ExperimentStatus {
    CREATED,
    IN_PROGRESS,
    PAUSED,
    COMPLETED
}

data class ExperimentSettings(
    val defaultRounds: Int = 5,
    val defaultResponseLength: Int = 150,
    val defaultCreativity: Float = 0.7f,
    val strictPersonaMode: Boolean = true,
    val debateIntensity: Float = 0.5f,
    val randomness: Float = 0.3f,
    val responseSpeed: Int = 1000, // ms per response
    val modelSource: ModelSource = ModelSource.GEMINI // new
)

enum class ModelSource {
    GEMINI,
    HUGGINGFACE,
    LOCAL
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val experimentId: String,
    val personaId: String,
    val content: String,
    val timestamp: Long,
    val turnNumber: Int,
    val messageType: MessageType
)

enum class MessageType {
    REGULAR,
    SYSTEM,
    SUMMARY
}

data class SummaryResult(
    val keyTakeaways: List<String>,
    val majorDisagreements: List<String>,
    val strongestIdeas: List<String>,
    val actionPlan: List<String>,
    val consensus: String,
    val openQuestions: List<String>
)
