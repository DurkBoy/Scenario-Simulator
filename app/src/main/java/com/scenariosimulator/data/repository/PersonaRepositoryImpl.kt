package com.scenariosimulator.data.repository

import com.scenariosimulator.data.local.PersonaDao
import com.scenariosimulator.data.mapper.toDomain
import com.scenariosimulator.data.mapper.toEntity
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.repository.PersonaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonaRepositoryImpl @Inject constructor(
    private val personaDao: PersonaDao
) : PersonaRepository {

    override suspend fun savePersona(persona: Persona) {
        personaDao.insert(persona.toEntity())
    }

    override suspend fun getPersona(id: String): Flow<Persona?> {
        return personaDao.getPersona(id).map { entity -> entity?.toDomain() }
    }

    override suspend fun getPersonaByIdSync(id: String): Persona? {
        return personaDao.getPersonaSync(id)?.toDomain()
    }

    override suspend fun getAllPersonas(): Flow<List<Persona>> {
        return personaDao.getAllPersonas().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAllPersonasList(): List<Persona> {
        return personaDao.getAllPersonas().firstOrNull()?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun deletePersona(id: String) {
        personaDao.deleteById(id)
    }

    override suspend fun getPresetPersonas(): List<Persona> {
        return listOf(
            Persona(
                name = "Dr. Ada Lovelace",
                role = "Scientist",
                expertise = "Computer Science, Mathematics",
                tone = "Analytical",
                worldview = "Optimistic",
                goals = "Advance human knowledge",
                bias = "Prefers data-driven approaches",
                creativityLevel = 0.8f,
                skepticismLevel = 0.3f,
                assertivenessLevel = 0.6f,
                collaborationLevel = 0.9f,
                avatarColor = androidx.compose.ui.graphics.Color(0xFF4CAF50)
            ),
            Persona(
                name = "Socrates",
                role = "Philosopher",
                expertise = "Ethics, Logic",
                tone = "Inquisitive",
                worldview = "Skeptical",
                goals = "Question assumptions",
                bias = "Distrusts conventional wisdom",
                creativityLevel = 0.5f,
                skepticismLevel = 0.9f,
                assertivenessLevel = 0.7f,
                collaborationLevel = 0.4f,
                avatarColor = androidx.compose.ui.graphics.Color(0xFFFF9800)
            ),
            Persona(
                name = "Elon Mask",
                role = "Engineer",
                expertise = "Spacecraft, AI",
                tone = "Bold",
                worldview = "Futuristic",
                goals = "Make humanity multiplanetary",
                bias = "Overconfidence in tech",
                creativityLevel = 0.9f,
                skepticismLevel = 0.2f,
                assertivenessLevel = 0.8f,
                collaborationLevel = 0.5f,
                avatarColor = androidx.compose.ui.graphics.Color(0xFF2196F3)
            ),
            Persona(
                name = "Machiavelli",
                role = "Strategist",
                expertise = "Political Strategy",
                tone = "Calculating",
                worldview = "Pragmatic",
                goals = "Achieve power and control",
                bias = "Ends justify means",
                creativityLevel = 0.6f,
                skepticismLevel = 0.7f,
                assertivenessLevel = 0.9f,
                collaborationLevel = 0.2f,
                avatarColor = androidx.compose.ui.graphics.Color(0xFFF44336)
            ),
            Persona(
                name = "Hypatia",
                role = "Mathematician",
                expertise = "Mathematics, Astronomy",
                tone = "Curious",
                worldview = "Rational",
                goals = "Discover universal truths",
                bias = "Trusts mathematical proofs",
                creativityLevel = 0.7f,
                skepticismLevel = 0.4f,
                assertivenessLevel = 0.5f,
                collaborationLevel = 0.8f,
                avatarColor = androidx.compose.ui.graphics.Color(0xFF9C27B0)
            )
        )
    }
}
