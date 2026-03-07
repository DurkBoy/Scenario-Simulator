package com.scenariosimulator.presentation.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.SummaryResult
import com.scenariosimulator.domain.repository.ExperimentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val experimentRepository: ExperimentRepository
) : ViewModel() {

    private val experimentId: String = savedStateHandle["experimentId"] ?: ""

    private val _experiment = MutableStateFlow<Experiment?>(null)
    val experiment: StateFlow<Experiment?> = _experiment.asStateFlow()

    private val _summary = MutableStateFlow<SummaryResult?>(null)
    val summary: StateFlow<SummaryResult?> = _summary.asStateFlow()

    init {
        loadExperiment()
    }

    private fun loadExperiment() {
        viewModelScope.launch {
            experimentRepository.getExperiment(experimentId).collect { experiment ->
                _experiment.value = experiment
                _summary.value = experiment?.summary
            }
        }
    }
}
