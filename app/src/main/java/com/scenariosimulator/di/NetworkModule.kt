package com.scenariosimulator.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.scenariosimulator.BuildConfig
import com.scenariosimulator.data.remote.gemini.GeminiApi
import com.scenariosimulator.data.remote.huggingface.HuggingFaceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Provides
    @Singleton
    fun provideHuggingFaceApi(client: OkHttpClient, gson: Gson): HuggingFaceApi {
        return Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(HuggingFaceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeminiApi(client: OkHttpClient, gson: Gson): GeminiApi {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GeminiApi::class.java)
    }
}
