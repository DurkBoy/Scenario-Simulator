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

    private val _allExperiments = MutableStateFlow<List<Experiment>>(emptyList())
    val allExperiments: StateFlow<List<Experiment>> = _allExperiments.asStateFlow()

    private val _recentExperiments = MutableStateFlow<List<Experiment>>(emptyList())
    val recentExperiments: StateFlow<List<Experiment>> = _recentExperiments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadExperiments()
    }

    private fun loadExperiments() {
        viewModelScope.launch {
            _isLoading.value = true
            experimentRepository.getAllExperiments().collect { experiments ->
                val sorted = experiments.sortedByDescending { it.createdAt }
                _allExperiments.value = sorted
                _recentExperiments.value = sorted.take(5)
                _isLoading.value = false
            }
        }
    }
}
