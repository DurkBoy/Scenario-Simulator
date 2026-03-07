package com.scenariosimulator.presentation.persona

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.model.Persona
import com.scenariosimulator.domain.repository.PersonaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PersonaBuilderViewModel @Inject constructor(
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val _personas = MutableStateFlow<List<Persona>>(emptyList())
    val personas: StateFlow<List<Persona>> = _personas.asStateFlow()

    private val _isEditing = MutableStateFlow<Persona?>(null)
    val isEditing: StateFlow<Persona?> = _isEditing.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _role = MutableStateFlow("")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _expertise = MutableStateFlow("")
    val expertise: StateFlow<String> = _expertise.asStateFlow()

    private val _tone = MutableStateFlow("")
    val tone: StateFlow<String> = _tone.asStateFlow()

    private val _worldview = MutableStateFlow("")
    val worldview: StateFlow<String> = _worldview.asStateFlow()

    private val _goals = MutableStateFlow("")
    val goals: StateFlow<String> = _goals.asStateFlow()

    private val _bias = MutableStateFlow("")
    val bias: StateFlow<String> = _bias.asStateFlow()

    private val _creativityLevel = MutableStateFlow(0.5f)
    val creativityLevel: StateFlow<Float> = _creativityLevel.asStateFlow()

    private val _skepticismLevel = MutableStateFlow(0.5f)
    val skepticismLevel: StateFlow<Float> = _skepticismLevel.asStateFlow()

    private val _assertivenessLevel = MutableStateFlow(0.5f)
    val assertivenessLevel: StateFlow<Float> = _assertivenessLevel.asStateFlow()

    private val _collaborationLevel = MutableStateFlow(0.5f)
    val collaborationLevel: StateFlow<Float> = _collaborationLevel.asStateFlow()

    private val _avatarColor = MutableStateFlow(Color(0xFF4CAF50))
    val avatarColor: StateFlow<Color> = _avatarColor.asStateFlow()

    init {
        loadPersonas()
    }

    private fun loadPersonas() {
        viewModelScope.launch {
            personaRepository.getAllPersonas().collect { personas ->
                _personas.value = personas
            }
        }
    }

    fun startEditing(persona: Persona? = null) {
        if (persona == null) {
            // New persona
            _name.value = ""
            _role.value = ""
            _expertise.value = ""
            _tone.value = ""
            _worldview.value = ""
            _goals.value = ""
            _bias.value = ""
            _creativityLevel.value = 0.5f
            _skepticismLevel.value = 0.5f
            _assertivenessLevel.value = 0.5f
            _collaborationLevel.value = 0.5f
            _avatarColor.value = Color(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                1f
            )
            _isEditing.value = null
        } else {
            // Edit existing
            _name.value = persona.name
            _role.value = persona.role
            _expertise.value = persona.expertise
            _tone.value = persona.tone
            _worldview.value = persona.worldview
            _goals.value = persona.goals
            _bias.value = persona.bias
            _creativityLevel.value = persona.creativityLevel
            _skepticismLevel.value = persona.skepticismLevel
            _assertivenessLevel.value = persona.assertivenessLevel
            _collaborationLevel.value = persona.collaborationLevel
            _avatarColor.value = persona.avatarColor
            _isEditing.value = persona
        }
    }

    fun cancelEditing() {
        _isEditing.value = null
    }

    fun updateName(newName: String) { _name.value = newName }
    fun updateRole(newRole: String) { _role.value = newRole }
    fun updateExpertise(newExpertise: String) { _expertise.value = newExpertise }
    fun updateTone(newTone: String) { _tone.value = newTone }
    fun updateWorldview(newWorldview: String) { _worldview.value = newWorldview }
    fun updateGoals(newGoals: String) { _goals.value = newGoals }
    fun updateBias(newBias: String) { _bias.value = newBias }
    fun updateCreativityLevel(level: Float) { _creativityLevel.value = level }
    fun updateSkepticismLevel(level: Float) { _skepticismLevel.value = level }
    fun updateAssertivenessLevel(level: Float) { _assertivenessLevel.value = level }
    fun updateCollaborationLevel(level: Float) { _collaborationLevel.value = level }
    fun updateAvatarColor(color: Color) { _avatarColor.value = color }

    fun savePersona() {
        val persona = Persona(
            id = _isEditing.value?.id ?: "",
            name = _name.value,
            role = _role.value,
            expertise = _expertise.value,
            tone = _tone.value,
            worldview = _worldview.value,
            goals = _goals.value,
            bias = _bias.value,
            creativityLevel = _creativityLevel.value,
            skepticismLevel = _skepticismLevel.value,
            assertivenessLevel = _assertivenessLevel.value,
            collaborationLevel = _collaborationLevel.value,
            avatarColor = _avatarColor.value
        )
        viewModelScope.launch {
            personaRepository.savePersona(persona)
            cancelEditing()
            loadPersonas()
        }
    }

    fun deletePersona(persona: Persona) {
        viewModelScope.launch {
            personaRepository.deletePersona(persona.id)
            loadPersonas()
        }
    }

    fun loadPresets() {
        viewModelScope.launch {
            val presets = personaRepository.getPresetPersonas()
            for (p in presets) {
                personaRepository.savePersona(p)
            }
            loadPersonas()
        }
    }
}
