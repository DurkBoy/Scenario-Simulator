package com.scenariosimulator.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.repository.ExperimentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val experimentRepository: ExperimentRepository
) : ViewModel() {

    private val _recentExperiments = MutableStateFlow<List<Experiment>>(emptyList())
    val recentExperiments: StateFlow<List<Experiment>> = _recentExperiments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadRecentExperiments()
    }

    private fun loadRecentExperiments() {
        viewModelScope.launch {
            _isLoading.value = true
            experimentRepository.getAllExperiments().collect { experiments ->
                _recentExperiments.value = experiments.take(5) // Show last 5
                _isLoading.value = false
            }
        }
    }

    fun startNewExperiment() {
        // Navigate to create experiment screen (handled by navigation)
    }
}
