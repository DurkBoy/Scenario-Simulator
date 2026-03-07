package com.scenariosimulator.data.remote.huggingface

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HuggingFaceApi {
    @POST("models/gpt2") // Example model, can be configurable
    suspend fun generate(
        @Header("Authorization") token: String,
        @Body request: HuggingFaceRequest
    ): List<HuggingFaceResponse>
}

data class HuggingFaceRequest(
    val inputs: String,
    val parameters: Map<String, Any> = mapOf(
        "max_new_tokens" to 150,
        "temperature" to 0.7
    )
)

data class HuggingFaceResponse(
    val generated_text: String
)
