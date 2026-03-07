package com.scenariosimulator.data.local.entity

data class SummaryResultEntity(
    val keyTakeaways: List<String>,
    val majorDisagreements: List<String>,
    val strongestIdeas: List<String>,
    val actionPlan: List<String>,
    val consensus: String,
    val openQuestions: List<String>
)
