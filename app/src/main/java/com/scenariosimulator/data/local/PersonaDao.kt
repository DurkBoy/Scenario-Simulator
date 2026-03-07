package com.scenariosimulator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scenariosimulator.data.local.entity.PersonaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(persona: PersonaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(personas: List<PersonaEntity>)

    @Query("SELECT * FROM personas WHERE id = :id")
    fun getPersona(id: String): Flow<PersonaEntity?>

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun getPersonaSync(id: String): PersonaEntity?

    @Query("SELECT * FROM personas")
    fun getAllPersonas(): Flow<List<PersonaEntity>>

    @Query("DELETE FROM personas WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM personas")
    suspend fun deleteAll()
}
