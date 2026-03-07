package com.scenariosimulator.domain.repository

import com.scenariosimulator.domain.model.Persona
import kotlinx.coroutines.flow.Flow

interface PersonaRepository {
    suspend fun savePersona(persona: Persona)
    suspend fun getPersona(id: String): Flow<Persona?>
    suspend fun getPersonaByIdSync(id: String): Persona?
    suspend fun getAllPersonas(): Flow<List<Persona>>
    suspend fun getAllPersonasList(): List<Persona>  // added for export
    suspend fun deletePersona(id: String)
    suspend fun getPresetPersonas(): List<Persona>
}
