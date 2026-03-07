package com.scenariosimulator.di

import com.scenariosimulator.data.service.GeminiAIService
import com.scenariosimulator.data.service.HuggingFaceAIService
import com.scenariosimulator.data.service.LocalAIService
import com.scenariosimulator.domain.service.AIServiceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIServiceModule {

    @Provides
    @Singleton
    fun provideAIServiceManager(
        gemini: GeminiAIService,
        huggingFace: HuggingFaceAIService,
        local: LocalAIService
    ): AIServiceManager {
        return AIServiceManager(gemini, huggingFace, local)
    }
}
