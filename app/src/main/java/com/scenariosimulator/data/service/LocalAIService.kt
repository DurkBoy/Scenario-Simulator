package com.scenariosimulator.data.service

import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.model.SummaryResult
import com.scenariosimulator.domain.service.AIService
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalAIService @Inject constructor() : AIService {

    override suspend fun generateResponse(
        experiment: Experiment,
        persona: Persona,
        conversationHistory: List<Message>,
        userPrompt: String
    ): String {
        val templates = listOf(
            "I propose that we focus on ${if (persona.creativityLevel > 0.7) "novel approaches" else "tried methods"}.",
            "From my perspective as a ${persona.role}, I think ${if (persona.skepticismLevel > 0.6) "we need more evidence" else "this is promising"}.",
            "My worldview (${persona.worldview}) suggests ${if (persona.assertivenessLevel > 0.7) "we must act now" else "we should deliberate further"}.",
            "I'd like to collaborate on ${if (persona.collaborationLevel > 0.7) "a joint solution" else "separate tracks"}.",
            "Given the discussion, I recommend ${if (persona.bias.contains("data")) "data-driven decisions" else "intuitive leaps"}."
        )
        return templates.random(Random(System.currentTimeMillis()))
    }

    override suspend fun generateSummary(
        experiment: Experiment,
        conversationHistory: List<Message>
    ): SummaryResult {
        // Extract key phrases from conversation (simulated)
        val allText = conversationHistory.joinToString(" ") { it.content }
        val words = allText.split(Regex("\\s+")).map { it.lowercase() }.toSet()

        val keyTakeaways = mutableListOf<String>()
        if (words.contains("mars") || words.contains("colonize"))
            keyTakeaways.add("Mars colonization is a central theme")
        if (words.contains("ethics") || words.contains("moral"))
            keyTakeaways.add("Ethical concerns were raised")
        if (words.contains("technology") || words.contains("engineering"))
            keyTakeaways.add("Technological feasibility discussed")

        val disagreements = mutableListOf<String>()
        if (words.contains("disagree") || words.contains("but") || words.contains("however"))
            disagreements.add("Participants had differing views on implementation")

        val strongIdeas = mutableListOf<String>()
        if (words.contains("terraform") || words.contains("atmosphere"))
            strongIdeas.add("Terraforming as a long-term goal")
        if (words.contains("robotics") || words.contains("automation"))
            strongIdeas.add("Robotic pre-deployment")

        val actionPlan = listOf(
            "Step 1: Further research",
            "Step 2: Stakeholder consultation",
            "Step 3: Pilot project"
        )

        val consensus = if (disagreements.isEmpty()) "Broad agreement on next steps" else "Partial agreement with reservations"
        val openQuestions = listOf("How to fund the project?", "What is the timeline?")

        return SummaryResult(
            keyTakeaways = if (keyTakeaways.isNotEmpty()) keyTakeaways else listOf("No clear takeaways identified"),
            majorDisagreements = disagreements,
            strongestIdeas = if (strongIdeas.isNotEmpty()) strongIdeas else listOf("No standout ideas"),
            actionPlan = actionPlan,
            consensus = consensus,
            openQuestions = openQuestions
        )
    }
}
