package com.scenariosimulator.domain.model

import android.net.Uri

data class User(
    val id: String,
    val emailOrGuestId: String,
    val displayName: String,
    val themeSettings: ThemeSettings,
    val savedPersonas: List<String>, // list of persona IDs
    val savedExperiments: List<String> // list of experiment IDs
)

data class ThemeSettings(
    val darkMode: Boolean = true,
    val accentColor: String = "#00FFFF", // neon cyan
    val animationIntensity: Float = 1.0f,
    val textSizeScale: Float = 1.0f
)
