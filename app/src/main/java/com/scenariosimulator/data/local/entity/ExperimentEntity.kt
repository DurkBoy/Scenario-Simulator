package com.scenariosimulator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "experiments")
data class ExperimentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val prompt: String,
    val mode: String, // enum as string
    val participants: List<String>, // list of persona IDs - stored as JSON string in TypeConverter
    val settings: String, // JSON string of ExperimentSettings
    val createdAt: Long,
    val status: String, // e.g., "in_progress", "completed"
    val transcript: String? = null, // JSON list of Message IDs or full messages? We'll store as JSON string
    val summary: String? = null // JSON string of SummaryResult
)
