package com.scenariosimulator.presentation.settings

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.repository.ExperimentRepository
import com.scenariosimulator.domain.repository.PersonaRepository
import com.scenariosimulator.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore("settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val experimentRepository: ExperimentRepository,
    private val personaRepository: PersonaRepository
) : ViewModel() {

    private val _darkMode = MutableStateFlow(true)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _accentColor = MutableStateFlow("#00FFFF")
    val accentColor: StateFlow<String> = _accentColor.asStateFlow()

    private val _animationIntensity = MutableStateFlow(1.0f)
    val animationIntensity: StateFlow<Float> = _animationIntensity.asStateFlow()

    private val _textSizeScale = MutableStateFlow(1.0f)
    val textSizeScale: StateFlow<Float> = _textSizeScale.asStateFlow()

    private val _defaultRounds = MutableStateFlow(5)
    val defaultRounds: StateFlow<Int> = _defaultRounds.asStateFlow()

    private val _defaultResponseLength = MutableStateFlow(150)
    val defaultResponseLength: StateFlow<Int> = _defaultResponseLength.asStateFlow()

    private val _defaultCreativity = MutableStateFlow(0.7f)
    val defaultCreativity: StateFlow<Float> = _defaultCreativity.asStateFlow()

    private val _strictPersonaMode = MutableStateFlow(true)
    val strictPersonaMode: StateFlow<Boolean> = _strictPersonaMode.asStateFlow()

    private val _debateIntensity = MutableStateFlow(0.5f)
    val debateIntensity: StateFlow<Float> = _debateIntensity.asStateFlow()

    private val _randomness = MutableStateFlow(0.3f)
    val randomness: StateFlow<Float> = _randomness.asStateFlow()

    private val _responseSpeed = MutableStateFlow(1000)
    val responseSpeed: StateFlow<Int> = _responseSpeed.asStateFlow()

    private val _modelSource = MutableStateFlow("GEMINI")
    val modelSource: StateFlow<String> = _modelSource.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _exportStatus = MutableStateFlow<String?>(null)
    val exportStatus: StateFlow<String?> = _exportStatus.asStateFlow()

    private val _importStatus = MutableStateFlow<String?>(null)
    val importStatus: StateFlow<String?> = _importStatus.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _darkMode.value = prefs[Keys.DARK_MODE] ?: true
                _accentColor.value = prefs[Keys.ACCENT_COLOR] ?: "#00FFFF"
                _animationIntensity.value = prefs[Keys.ANIMATION_INTENSITY] ?: 1.0f
                _textSizeScale.value = prefs[Keys.TEXT_SIZE_SCALE] ?: 1.0f
                _defaultRounds.value = prefs[Keys.DEFAULT_ROUNDS] ?: 5
                _defaultResponseLength.value = prefs[Keys.DEFAULT_RESPONSE_LENGTH] ?: 150
                _defaultCreativity.value = prefs[Keys.DEFAULT_CREATIVITY] ?: 0.7f
                _strictPersonaMode.value = prefs[Keys.STRICT_PERSONA_MODE] ?: true
                _debateIntensity.value = prefs[Keys.DEBATE_INTENSITY] ?: 0.5f
                _randomness.value = prefs[Keys.RANDOMNESS] ?: 0.3f
                _responseSpeed.value = prefs[Keys.RESPONSE_SPEED] ?: 1000
                _modelSource.value = prefs[Keys.MODEL_SOURCE] ?: "GEMINI"
                _notificationsEnabled.value = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
            }
        }
    }

    fun updateDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
        viewModelScope.launch { context.dataStore.edit { it[Keys.DARK_MODE] = enabled } }
    }

    fun updateAccentColor(color: String) {
        _accentColor.value = color
        viewModelScope.launch { context.dataStore.edit { it[Keys.ACCENT_COLOR] = color } }
    }

    fun updateAnimationIntensity(value: Float) {
        _animationIntensity.value = value
        viewModelScope.launch { context.dataStore.edit { it[Keys.ANIMATION_INTENSITY] = value } }
    }

    fun updateTextSizeScale(value: Float) {
        _textSizeScale.value = value
        viewModelScope.launch { context.dataStore.edit { it[Keys.TEXT_SIZE_SCALE] = value } }
    }

    fun updateDefaultRounds(rounds: Int) {
        _defaultRounds.value = rounds
        viewModelScope.launch { context.dataStore.edit { it[Keys.DEFAULT_ROUNDS] = rounds } }
    }

    fun updateDefaultResponseLength(length: Int) {
        _defaultResponseLength.value = length
        viewModelScope.launch { context.dataStore.edit { it[Keys.DEFAULT_RESPONSE_LENGTH] = length } }
    }

    fun updateDefaultCreativity(creativity: Float) {
        _defaultCreativity.value = creativity
        viewModelScope.launch { context.dataStore.edit { it[Keys.DEFAULT_CREATIVITY] = creativity } }
    }

    fun updateStrictPersonaMode(enabled: Boolean) {
        _strictPersonaMode.value = enabled
        viewModelScope.launch { context.dataStore.edit { it[Keys.STRICT_PERSONA_MODE] = enabled } }
    }

    fun updateDebateIntensity(intensity: Float) {
        _debateIntensity.value = intensity
        viewModelScope.launch { context.dataStore.edit { it[Keys.DEBATE_INTENSITY] = intensity } }
    }

    fun updateRandomness(randomness: Float) {
        _randomness.value = randomness
        viewModelScope.launch { context.dataStore.edit { it[Keys.RANDOMNESS] = randomness } }
    }

    fun updateResponseSpeed(speed: Int) {
        _responseSpeed.value = speed
        viewModelScope.launch { context.dataStore.edit { it[Keys.RESPONSE_SPEED] = speed } }
    }

    fun updateModelSource(source: String) {
        _modelSource.value = source
        viewModelScope.launch { context.dataStore.edit { it[Keys.MODEL_SOURCE] = source } }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        viewModelScope.launch { context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled } }
    }

    fun clearCache() {
        viewModelScope.launch {
            context.dataStore.edit { it.clear() }
            _exportStatus.value = "Cache cleared"
        }
    }

    fun exportData(uri: Uri, type: ExportType) {
        viewModelScope.launch {
            val experiments = experimentRepository.getAllExperimentsList()
            val personas = personaRepository.getAllPersonasList()

            val data = when (type) {
                ExportType.EXPERIMENTS -> FileUtils.exportExperimentsToJson(experiments)
                ExportType.PERSONAS -> FileUtils.exportPersonasToJson(personas)
                ExportType.ALL -> FileUtils.exportAllToJson(experiments, personas)
            }

            val success = FileUtils.saveToUri(context, uri, data)
            _exportStatus.value = if (success) "Export successful" else "Export failed"
        }
    }

    fun importData(uri: Uri, type: ExportType) {
        viewModelScope.launch {
            val json = FileUtils.readFromUri(context, uri) ?: run {
                _importStatus.value = "Failed to read file"
                return@launch
            }

            try {
                when (type) {
                    ExportType.EXPERIMENTS -> {
                        val experiments = FileUtils.importExperimentsFromJson(json)
                        experiments.forEach { experimentRepository.saveExperiment(it) }
                        _importStatus.value = "Imported ${experiments.size} experiments"
                    }
                    ExportType.PERSONAS -> {
                        val personas = FileUtils.importPersonasFromJson(json)
                        personas.forEach { personaRepository.savePersona(it) }
                        _importStatus.value = "Imported ${personas.size} personas"
                    }
                    ExportType.ALL -> {
                        val payload = FileUtils.importAllFromJson(json)
                        payload.experiments.forEach { experimentRepository.saveExperiment(it) }
                        payload.personas.forEach { personaRepository.savePersona(it) }
                        _importStatus.value =
                            "Imported ${payload.experiments.size} experiments and ${payload.personas.size} personas"
                    }
                }
            } catch (e: Exception) {
                _importStatus.value = "Import failed: ${e.message ?: "unknown error"}"
            }
        }
    }

    enum class ExportType {
        EXPERIMENTS, PERSONAS, ALL
    }

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val ANIMATION_INTENSITY = floatPreferencesKey("animation_intensity")
        val TEXT_SIZE_SCALE = floatPreferencesKey("text_size_scale")
        val DEFAULT_ROUNDS = intPreferencesKey("default_rounds")
        val DEFAULT_RESPONSE_LENGTH = intPreferencesKey("default_response_length")
        val DEFAULT_CREATIVITY = floatPreferencesKey("default_creativity")
        val STRICT_PERSONA_MODE = booleanPreferencesKey("strict_persona_mode")
        val DEBATE_INTENSITY = floatPreferencesKey("debate_intensity")
        val RANDOMNESS = floatPreferencesKey("randomness")
        val RESPONSE_SPEED = intPreferencesKey("response_speed")
        val MODEL_SOURCE = stringPreferencesKey("model_source")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }
}
