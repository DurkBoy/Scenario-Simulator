package com.scenariosimulator.di

import com.scenariosimulator.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    @Named("geminiApiKey")
    fun provideGeminiApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    @Provides
    @Singleton
    @Named("huggingfaceApiToken")
    fun provideHuggingFaceApiToken(): String {
        return BuildConfig.HUGGINGFACE_API_TOKEN
    }
}
