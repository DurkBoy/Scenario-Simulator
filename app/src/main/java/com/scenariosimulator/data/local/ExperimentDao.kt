package com.scenariosimulator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scenariosimulator.data.local.entity.ExperimentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExperimentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(experiment: ExperimentEntity)

    @Query("SELECT * FROM experiments WHERE id = :id")
    fun getExperiment(id: String): Flow<ExperimentEntity?>

    @Query("SELECT * FROM experiments ORDER BY createdAt DESC")
    fun getAllExperiments(): Flow<List<ExperimentEntity>>

    @Query("DELETE FROM experiments WHERE id = :id")
    suspend fun deleteById(id: String)
}
