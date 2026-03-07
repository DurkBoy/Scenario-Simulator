package com.scenariosimulator.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromExperimentSettings(settings: ExperimentSettingsEntity): String {
        return gson.toJson(settings)
    }

    @TypeConverter
    fun toExperimentSettings(settingsJson: String): ExperimentSettingsEntity {
        val type = object : TypeToken<ExperimentSettingsEntity>() {}.type
        return gson.fromJson(settingsJson, type)
    }

    @TypeConverter
    fun fromMessageList(messages: List<MessageEntity>): String {
        return gson.toJson(messages)
    }

    @TypeConverter
    fun toMessageList(messagesJson: String): List<MessageEntity> {
        val type = object : TypeToken<List<MessageEntity>>() {}.type
        return gson.fromJson(messagesJson, type) ?: emptyList()
    }

    @TypeConverter
    fun fromSummaryResult(summary: SummaryResultEntity?): String? {
        return summary?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSummaryResult(summaryJson: String?): SummaryResultEntity? {
        if (summaryJson.isNullOrBlank()) return null
        val type = object : TypeToken<SummaryResultEntity>() {}.type
        return gson.fromJson(summaryJson, type)
    }
}

// Helper data classes for JSON storage
data class ExperimentSettingsEntity(
    val defaultRounds: Int,
    val defaultResponseLength: Int,
    val defaultCreativity: Float,
    val strictPersonaMode: Boolean,
    val debateIntensity: Float,
    val randomness: Float,
    val responseSpeed: Int,
    val modelSource: String
)

data class SummaryResultEntity(
    val keyTakeaways: List<String>,
    val majorDisagreements: List<String>,
    val strongestIdeas: List<String>,
    val actionPlan: List<String>,
    val consensus: String,
    val openQuestions: List<String>
)

data class MessageEntity(
    val id: String,
    val experimentId: String,
    val personaId: String,
    val content: String,
    val timestamp: Long,
    val turnNumber: Int,
    val messageType: String // "regular", "system", "summary"
)
