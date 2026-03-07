package com.scenariosimulator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val role: String,
    val expertise: String,
    val tone: String,
    val worldview: String,
    val goals: String,
    val bias: String,
    val creativityLevel: Float,
    val skepticismLevel: Float,
    val assertivenessLevel: Float,
    val collaborationLevel: Float,
    val avatarColorHex: String // store as #RRGGBB or #AARRGGBB
)
