package com.scenariosimulator.data.repository

import com.google.gson.Gson
import com.scenariosimulator.data.local.ExperimentDao
import com.scenariosimulator.data.local.entity.ExperimentEntity
import com.scenariosimulator.data.local.entity.ExperimentSettingsEntity
import com.scenariosimulator.data.local.entity.MessageEntity
import com.scenariosimulator.data.local.entity.SummaryResultEntity
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.ExperimentMode
import com.scenariosimulator.domain.model.ExperimentSettings
import com.scenariosimulator.domain.model.ExperimentStatus
import com.scenariosimulator.domain.model.Message
import com.scenariosimulator.domain.model.MessageType
import com.scenariosimulator.domain.model.ModelSource
import com.scenariosimulator.domain.model.SummaryResult
import com.scenariosimulator.domain.repository.ExperimentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExperimentRepositoryImpl @Inject constructor(
    private val experimentDao: ExperimentDao,
    private val gson: Gson
) : ExperimentRepository {

    override suspend fun saveExperiment(experiment: Experiment) {
        experimentDao.insert(experiment.toEntity())
    }

    override suspend fun getExperiment(id: String): Flow<Experiment?> {
        return experimentDao.getExperiment(id).map { entity -> entity?.toDomain() }
    }

    override suspend fun getAllExperiments(): Flow<List<Experiment>> {
        return experimentDao.getAllExperiments().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getAllExperimentsList(): List<Experiment> {
        return experimentDao.getAllExperiments().firstOrNull()?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun deleteExperiment(id: String) {
        experimentDao.deleteById(id)
    }

    private fun Experiment.toEntity(): ExperimentEntity {
        val settingsEntity = ExperimentSettingsEntity(
            defaultRounds = settings.defaultRounds,
            defaultResponseLength = settings.defaultResponseLength,
            defaultCreativity = settings.defaultCreativity,
            strictPersonaMode = settings.strictPersonaMode,
            debateIntensity = settings.debateIntensity,
            randomness = settings.randomness,
            responseSpeed = settings.responseSpeed,
            modelSource = settings.modelSource.name
        )
        val messageEntities = transcript.map { msg ->
            MessageEntity(
                id = msg.id,
                experimentId = msg.experimentId,
                personaId = msg.personaId,
                content = msg.content,
                timestamp = msg.timestamp,
                turnNumber = msg.turnNumber,
                messageType = msg.messageType.name
            )
        }
        val summaryEntity = summary?.let {
            SummaryResultEntity(
                keyTakeaways = it.keyTakeaways,
                majorDisagreements = it.majorDisagreements,
                strongestIdeas = it.strongestIdeas,
                actionPlan = it.actionPlan,
                consensus = it.consensus,
                openQuestions = it.openQuestions
            )
        }
        return ExperimentEntity(
            id = id,
            title = title,
            prompt = prompt,
            mode = mode.name,
            participants = participantIds,
            settings = gson.toJson(settingsEntity),
            createdAt = createdAt,
            status = status.name,
            transcript = gson.toJson(messageEntities),
            summary = summaryEntity?.let { gson.toJson(it) }
        )
    }

    private fun ExperimentEntity.toDomain(): Experiment {
        val settingsEntity = gson.fromJson(settings, ExperimentSettingsEntity::class.java)
        val settings = ExperimentSettings(
            defaultRounds = settingsEntity.defaultRounds,
            defaultResponseLength = settingsEntity.defaultResponseLength,
            defaultCreativity = settingsEntity.defaultCreativity,
            strictPersonaMode = settingsEntity.strictPersonaMode,
            debateIntensity = settingsEntity.debateIntensity,
            randomness = settingsEntity.randomness,
            responseSpeed = settingsEntity.responseSpeed,
            modelSource = ModelSource.valueOf(settingsEntity.modelSource)
        )
        val messageEntities: List<MessageEntity> = gson.fromJson(transcript, Array<MessageEntity>::class.java)?.toList() ?: emptyList()
        val transcript = messageEntities.map { msg ->
            Message(
                id = msg.id,
                experimentId = msg.experimentId,
                personaId = msg.personaId,
                content = msg.content,
                timestamp = msg.timestamp,
                turnNumber = msg.turnNumber,
                messageType = MessageType.valueOf(msg.messageType)
            )
        }
        val summaryEntity = summary?.let { gson.fromJson(it, SummaryResultEntity::class.java) }
        val summary = summaryEntity?.let {
            SummaryResult(
                keyTakeaways = it.keyTakeaways,
                majorDisagreements = it.majorDisagreements,
                strongestIdeas = it.strongestIdeas,
                actionPlan = it.actionPlan,
                consensus = it.consensus,
                openQuestions = it.openQuestions
            )
        }
        return Experiment(
            id = id,
            title = title,
            prompt = prompt,
            mode = ExperimentMode.valueOf(mode),
            participantIds = participants,
            settings = settings,
            createdAt = createdAt,
            status = ExperimentStatus.valueOf(status),
            transcript = transcript,
            summary = summary
        )
    }
}
