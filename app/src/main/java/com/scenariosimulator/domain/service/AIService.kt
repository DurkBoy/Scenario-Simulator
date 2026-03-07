package com.scenariosimulator.domain.service

import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.model.SummaryResult

interface AIService {
    suspend fun generateResponse(
        experiment: Experiment,
        persona: Persona,
        conversationHistory: List<Message>,
        userPrompt: String
    ): String

    suspend fun generateSummary(
        experiment: Experiment,
        conversationHistory: List<Message>
    ): SummaryResult
}
