package com.scenariosimulator.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.scenariosimulator.domain.model.Experiment
import com.scenariosimulator.domain.model.Persona
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

data class BackupPayload(
    val experiments: List<Experiment> = emptyList(),
    val personas: List<Persona> = emptyList()
)

object FileUtils {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportExperimentsToJson(experiments: List<Experiment>): String =
        gson.toJson(experiments)

    fun exportPersonasToJson(personas: List<Persona>): String =
        gson.toJson(personas)

    fun exportAllToJson(experiments: List<Experiment>, personas: List<Persona>): String =
        gson.toJson(BackupPayload(experiments = experiments, personas = personas))

    fun importExperimentsFromJson(json: String): List<Experiment> {
        val type = object : TypeToken<List<Experiment>>() {}.type
        return gson.fromJson<List<Experiment>>(json, type) ?: emptyList()
    }

    fun importPersonasFromJson(json: String): List<Persona> {
        val type = object : TypeToken<List<Persona>>() {}.type
        return gson.fromJson<List<Persona>>(json, type) ?: emptyList()
    }

    fun importAllFromJson(json: String): BackupPayload {
        val type = object : TypeToken<BackupPayload>() {}.type
        return gson.fromJson<BackupPayload>(json, type) ?: BackupPayload()
    }

    suspend fun saveToUri(context: Context, uri: Uri, data: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(data)
                    writer.flush()
                }
            } != null
        } catch (_: Exception) {
            false
        }
    }

    suspend fun readFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
