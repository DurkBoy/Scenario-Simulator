package com.scenariosimulator.data.mapper

import androidx.compose.ui.graphics.Color
import com.scenariosimulator.data.local.entity.PersonaEntity
import com.scenariosimulator.domain.model.Persona

fun PersonaEntity.toDomain(): Persona {
    return Persona(
        id = id,
        name = name,
        role = role,
        expertise = expertise,
        tone = tone,
        worldview = worldview,
        goals = goals,
        bias = bias,
        creativityLevel = creativityLevel,
        skepticismLevel = skepticismLevel,
        assertivenessLevel = assertivenessLevel,
        collaborationLevel = collaborationLevel,
        avatarColor = Color(android.graphics.Color.parseColor(avatarColorHex))
    )
}

fun Persona.toEntity(): PersonaEntity {
    return PersonaEntity(
        id = id,
        name = name,
        role = role,
        expertise = expertise,
        tone = tone,
        worldview = worldview,
        goals = goals,
        bias = bias,
        creativityLevel = creativityLevel,
        skepticismLevel = skepticismLevel,
        assertivenessLevel = assertivenessLevel,
        collaborationLevel = collaborationLevel,
        avatarColorHex = avatarColor.toHexString()
    )
}

// Helper to convert Compose Color to hex
fun Color.toHexString(): String {
    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()
    val alpha = (alpha * 255).toInt()
    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}
