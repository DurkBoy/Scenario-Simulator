package com.scenariosimulator.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scenariosimulator.data.local.entity.ExperimentEntity
import com.scenariosimulator.data.local.entity.PersonaEntity

@Database(
    entities = [PersonaEntity::class, ExperimentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun experimentDao(): ExperimentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scenario_simulator.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
