package com.scenariosimulator.data.service

import com.google.gson.Gson
import com.scenariosimulator.data.remote.gemini.Content
import com.scenariosimulator.data.remote.gemini.GeminiApi
import com.scenariosimulator.data.remote.gemini.GeminiRequest
import com.scenariosimulator.data.remote.gemini.Part
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.model.SummaryResult
import com.scenariosimulator.domain.service.AIService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiAIService @Inject constructor(
    private val geminiApi: GeminiApi,
    @Named("geminiApiKey") private val apiKey: String,
    private val gson: Gson
) : AIService {

    override suspend fun generateResponse(
        experiment: Experiment,
        persona: Persona,
        conversationHistory: List<Message>,
        userPrompt: String
    ): String {
        val prompt = buildResponsePrompt(experiment, persona, conversationHistory, userPrompt)
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        val response = geminiApi.generateContent(apiKey, request)
        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "No response generated."
    }

    override suspend fun generateSummary(
        experiment: Experiment,
        conversationHistory: List<Message>
    ): SummaryResult {
        val prompt = buildSummaryPrompt(experiment, conversationHistory)
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        val response = geminiApi.generateContent(apiKey, request)
        val json = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "{}"
        return try {
            gson.fromJson(json, SummaryResult::class.java)
        } catch (e: Exception) {
            SummaryResult(
                keyTakeaways = listOf("Summary generation failed"),
                majorDisagreements = emptyList(),
                strongestIdeas = emptyList(),
                actionPlan = emptyList(),
                consensus = "No consensus reached.",
                openQuestions = emptyList()
            )
        }
    }

    private fun buildResponsePrompt(
        experiment: Experiment,
        persona: Persona,
        conversationHistory: List<Message>,
        userPrompt: String
    ): String {
        val history = conversationHistory.joinToString("\n") { msg ->
            "${msg.personaId}: ${msg.content}"
        }
        return """
            You are a persona with the following characteristics:
            Name: ${persona.name}
            Role: ${persona.role}
            Expertise: ${persona.expertise}
            Tone: ${persona.tone}
            Worldview: ${persona.worldview}
            Goals: ${persona.goals}
            Bias: ${persona.bias}
            Creativity Level: ${persona.creativityLevel}
            Skepticism Level: ${persona.skepticismLevel}
            Assertiveness Level: ${persona.assertivenessLevel}
            Collaboration Level: ${persona.collaborationLevel}

            Experiment Mode: ${experiment.mode}
            Original User Prompt: ${experiment.prompt}

            Conversation so far:
            $history

            Now, respond as your persona to the latest prompt or the previous messages. Stay in character.
            Your response:
        """.trimIndent()
    }

    private fun buildSummaryPrompt(
        experiment: Experiment,
        conversationHistory: List<Message>
    ): String {
        val history = conversationHistory.joinToString("\n") { msg ->
            "${msg.personaId}: ${msg.content}"
        }
        return """
            You are a neutral summarizer. Given the following conversation about the topic "${experiment.prompt}", produce a structured summary with:
            - Key takeaways
            - Major disagreements
            - Strongest ideas
            - Action plan
            - Consensus
            - Open questions

            Conversation:
            $history

            Provide the summary in JSON format with the following keys exactly: keyTakeaways (list of strings), majorDisagreements (list), strongestIdeas (list), actionPlan (list), consensus (string), openQuestions (list).
            Only output valid JSON.
        """.trimIndent()
    }
}
