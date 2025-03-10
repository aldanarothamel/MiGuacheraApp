package com.example.miguachera

import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object RecordUtils {
    fun updateCalfRecord(records: JSONArray, calfId: String, newData: JSONObject): JSONArray {
        val updatedRecords = JSONArray()
        var recordUpdated = false

        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            if (record.getString("id").equals(calfId, ignoreCase = true)) {
                // Actualizar el registro del ternero con los nuevos datos
                for (key in newData.keys()) {
                    record.put(key, newData.get(key))
                }
                updatedRecords.put(record)
                recordUpdated = true
            } else {
                updatedRecords.put(record)
            }
        }

        // Si no se encontró el ternero, agregar un nuevo registro
        if (!recordUpdated) {
            val newRecord = JSONObject()
            newRecord.put("id", calfId)
            for (key in newData.keys()) {
                newRecord.put(key, newData.get(key))
            }
            updatedRecords.put(newRecord)
        }

        return updatedRecords
    }

    fun readFromFile(fileName: String, filesDir: File): JSONArray {
        val file = File(filesDir, fileName)
        return if (file.exists()) {
            try {
                FileInputStream(file).bufferedReader().use { it.readText() }
                    .let { JSONArray(it) }
            } catch (e: IOException) {
                e.printStackTrace()
                JSONArray()
            }
        } else {
            JSONArray()
        }
    }

    fun saveToFile(fileName: String, filesDir: File, records: JSONArray): Boolean {
        return try {
            val file = File(filesDir, fileName)
            FileOutputStream(file).use { fos ->
                fos.write(records.toString().toByteArray())
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}