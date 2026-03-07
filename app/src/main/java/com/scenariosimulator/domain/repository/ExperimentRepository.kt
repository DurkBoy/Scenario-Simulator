package com.scenariosimulator.domain.repository

import com.scenariosimulator.domain.model.Experiment
import kotlinx.coroutines.flow.Flow

interface ExperimentRepository {
    suspend fun saveExperiment(experiment: Experiment)
    suspend fun getExperiment(id: String): Flow<Experiment?>
    suspend fun getAllExperiments(): Flow<List<Experiment>>
    suspend fun getAllExperimentsList(): List<Experiment>  // added for export
    suspend fun deleteExperiment(id: String)
}
