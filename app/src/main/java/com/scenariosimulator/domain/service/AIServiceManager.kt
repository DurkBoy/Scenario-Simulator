package com.scenariosimulator.domain.service

import com.scenariosimulator.data.service.GeminiAIService
import com.scenariosimulator.data.service.HuggingFaceAIService
import com.scenariosimulator.data.service.LocalAIService
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.ModelSource
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.model.SummaryResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIServiceManager @Inject constructor(
    private val geminiService: GeminiAIService,
    private val huggingFaceService: HuggingFaceAIService,
    private val localService: LocalAIService
) : AIService {

    override suspend fun generateResponse(
        experiment: Experiment,
        persona: Persona,
        conversationHistory: List<Message>,
        userPrompt: String
    ): String {
        val service = when (experiment.settings.modelSource) {
            ModelSource.GEMINI -> geminiService
            ModelSource.HUGGINGFACE -> huggingFaceService
            ModelSource.LOCAL -> localService
        }
        return service.generateResponse(experiment, persona, conversationHistory, userPrompt)
    }

    override suspend fun generateSummary(
        experiment: Experiment,
        conversationHistory: List<Message>
    ): SummaryResult {
        val service = when (experiment.settings.modelSource) {
            ModelSource.GEMINI -> geminiService
            ModelSource.HUGGINGFACE -> huggingFaceService
            ModelSource.LOCAL -> localService
        }
        return service.generateSummary(experiment, conversationHistory)
    }
}
