package com.scenariosimulator.data.local.entity

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
