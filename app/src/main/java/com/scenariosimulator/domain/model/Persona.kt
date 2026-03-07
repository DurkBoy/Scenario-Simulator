package com.scenariosimulator.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Persona(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    val expertise: String,
    val tone: String, // e.g., "assertive", "curious"
    val worldview: String, // e.g., "optimistic", "skeptical"
    val goals: String,
    val bias: String,
    val creativityLevel: Float, // 0.0 - 1.0
    val skepticismLevel: Float, // 0.0 - 1.0
    val assertivenessLevel: Float, // 0.0 - 1.0
    val collaborationLevel: Float, // 0.0 - 1.0
    val avatarColor: Color // stored as ARGB long maybe, but for simplicity we use String
) {
    fun toAvatarColorHex(): String {
        return "#${Integer.toHexString(avatarColor.hashCode())}" // Not ideal, but we'll store as hex
    }

    companion object {
        fun fromAvatarColorHex(hex: String): Color {
            return Color(android.graphics.Color.parseColor(hex))
        }
    }
}
