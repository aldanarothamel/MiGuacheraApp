package com.example.miguachera

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class SeasonInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_info)

        val seasonInfoContainer: LinearLayout = findViewById(R.id.seasonInfoContainer)

        // Leer los registros desde los archivos JSON
        val birthRecords = RecordUtils.readFromFile("birth_records.json", filesDir)
        val treatmentRecords = RecordUtils.readFromFile("treatment_records.json", filesDir)
        val deathRecords = RecordUtils.readFromFile("death_records.json", filesDir)

        // Consolidar los registros por ternero
        val consolidatedRecords = consolidateRecords(birthRecords, treatmentRecords, deathRecords)

        // Mostrar los registros en el LinearLayout
        for (record in consolidatedRecords) {
            val textView = TextView(this).apply {
                text = record.toString(4) // Formatear JSON para una mejor visualización
                textSize = 16f
                setPadding(0, 0, 0, 16)
            }
            seasonInfoContainer.addView(textView)
        }
    }

    // Función para consolidar los registros por ternero
    private fun consolidateRecords(birthRecords: JSONArray, treatmentRecords: JSONArray, deathRecords: JSONArray): List<JSONObject> {
        val consolidatedMap = mutableMapOf<String, JSONObject>()

        // Agregar registros de nacimientos
        for (i in 0 until birthRecords.length()) {
            val record = birthRecords.getJSONObject(i)
            val id = record.getString("id")
            consolidatedMap[id] = record
        }

        // Agregar registros de tratamientos
        for (i in 0 until treatmentRecords.length()) {
            val record = treatmentRecords.getJSONObject(i)
            val id = record.getString("id")
            val calfRecord = consolidatedMap.getOrPut(id) { JSONObject().put("id", id) }
            val treatments = calfRecord.optJSONArray("Tratamientos") ?: JSONArray().also { calfRecord.put("Tratamientos", it) }
            treatments.put(record)
        }

        // Agregar registros de fallecimientos
        for (i in 0 until deathRecords.length()) {
            val record = deathRecords.getJSONObject(i)
            val id = record.getString("id")
            val calfRecord = consolidatedMap.getOrPut(id) { JSONObject().put("id", id) }
            calfRecord.put("Fallecimiento", record)
        }

        return consolidatedMap.values.toList()
    }
}