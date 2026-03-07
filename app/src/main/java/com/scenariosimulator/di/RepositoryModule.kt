package com.scenariosimulator.di

import com.scenariosimulator.data.local.AppDatabase
import com.scenariosimulator.data.local.ExperimentDao
import com.scenariosimulator.data.local.PersonaDao
import com.scenariosimulator.data.repository.ExperimentRepositoryImpl
import com.scenariosimulator.data.repository.PersonaRepositoryImpl
import com.scenariosimulator.domain.repository.ExperimentRepository
import com.scenariosimulator.domain.repository.PersonaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePersonaDao(database: AppDatabase): PersonaDao {
        return database.personaDao()
    }

    @Provides
    @Singleton
    fun provideExperimentDao(database: AppDatabase): ExperimentDao {
        return database.experimentDao()
    }

    @Provides
    @Singleton
    fun providePersonaRepository(dao: PersonaDao): PersonaRepository {
        return PersonaRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideExperimentRepository(dao: ExperimentDao, gson: com.google.gson.Gson): ExperimentRepository {
        return ExperimentRepositoryImpl(dao, gson)
    }
}
