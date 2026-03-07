package com.scenariosimulator.data.local.entity

data class MessageEntity(
    val id: String,
    val experimentId: String,
    val personaId: String,
    val content: String,
    val timestamp: Long,
    val turnNumber: Int,
    val messageType: String // "regular", "system", "summary"
)
