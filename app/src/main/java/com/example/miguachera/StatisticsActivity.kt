package com.example.miguachera

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        val totalCalvesTextView: TextView = findViewById(R.id.totalCalves)
        val totalMalesTextView: TextView = findViewById(R.id.totalMales)
        val totalFemalesTextView: TextView = findViewById(R.id.totalFemales)
        val totalDeathsTextView: TextView = findViewById(R.id.totalDeaths)
        val totalSalesTextView: TextView = findViewById(R.id.totalSales)
        val totalFemalesAliveTextView: TextView = findViewById(R.id.totalFemalesAlive)
        val totalFinalTextView: TextView = findViewById(R.id.totalFinal)

        // Leer los datos existentes
        val records = readFromFile("birth_records.json")
        val sales = readFromFile("sales.json")

        // Calcular estadísticas generales
        val totalCalves = records.length()
        var totalMales = 0
        var totalFemales = 0
        var totalNonSex = 0
        var totalDeaths = 0
        var totalFemalesAlive = 0
        var totalMalesAlive = 0
        val totalSales = sales.length()

        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            when (record.optString("Sexo")) {
                "Macho" -> totalMales++
                "Hembra" -> totalFemales++
                "No visible" -> totalNonSex++
            }
            if (record.optString("Estado General al Nacer") == "Sin Vida" || record.optString("Actualización de Estado") == "Fallecido") {
                totalDeaths++
            }
        }

        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)

            if (record.optString("Sexo") == "Hembra" && (record.optString("Estado General al Nacer") != "Sin Vida" && record.optString("Actualización de Estado") != "Fallecido") ) {
                totalFemalesAlive++
            }
        }

        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)

            if (record.optString("Sexo") == "Macho" && (record.optString("Estado General al Nacer") != "Sin Vida" && record.optString("Actualización de Estado") != "Fallecido") ) {
                totalMalesAlive++
            }
        }



        val totalFinal = totalCalves - totalDeaths - totalSales

        totalCalvesTextView.text = "Total de Nacimientos: $totalCalves"
        totalFemalesTextView.text = "Total hembras nacidas: $totalFemales"
        totalMalesTextView.text = "Total machos nacidos: $totalMales"
        totalDeathsTextView.text = "Total de fallecimientos: $totalDeaths"
        totalSalesTextView.text = "Total de vendidos: $totalSales"
        totalFemalesAliveTextView.text = "Total hembras vivas: $totalFemalesAlive"
        totalFinalTextView.text = "Total Final: $totalFinal"

        // Botón para compartir todas las estadísticas
        val shareButton: Button = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getStatisticsText(totalCalves, totalFemales, totalMales, totalDeaths, totalSales, totalFemalesAlive, totalMalesAlive, totalFinal))
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir estadísticas"))
        }

        // Botón para compartir estadísticas del día
        val shareTodayButton: Button = findViewById(R.id.shareTodayButton)
        shareTodayButton.setOnClickListener {
            // Obtener los registros del día actual
            val todayRecords = getTodayRecords(records)

            if (todayRecords.isNotEmpty()) {
                val shareTodayIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getTodayStatisticsText(todayRecords))
                }
                startActivity(Intent.createChooser(shareTodayIntent, "Compartir estadísticas del día"))
            } else {
                Toast.makeText(this, "No hay registros para el día de hoy.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para leer los datos desde el archivo JSON
    private fun readFromFile(fileName: String): JSONArray {
        val file = File(filesDir, fileName)
        return if (file.exists()) {
            try {
                FileInputStream(file).bufferedReader().use { reader ->
                    JSONArray(reader.readText())
                }
            } catch (e: IOException) {
                e.printStackTrace()
                JSONArray()
            }
        } else {
            JSONArray()
        }
    }

    // Función para generar el texto de las estadísticas
    private fun getStatisticsText(totalCalves: Int, totalFemales: Int, totalMales: Int, totalDeaths: Int, totalSales: Int, totalFemalesAlive: Int, totalMalesAlive: Int, totalFinal: Int): String {
        return """
            Datos de la temporada:
            
            Total de Nacimientos: $totalCalves
            Total de Fallecimientos: $totalDeaths
            
            Total Hembras nacidas: $totalFemales
            Total Hembras vivas: $totalFemalesAlive
            
            Total Machos nacidos: $totalMales
            
            Total de Vendidos: $totalSales
            
            Total Final: $totalFinal
        """.trimIndent()
    }

    // Función para obtener los registros del día actual
    private fun getTodayRecords(records: JSONArray): List<JSONObject> {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val todayRecords = mutableListOf<JSONObject>()

        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            val birthDate = record.optString("Fecha de Nacimiento")


            if (birthDate == currentDate) {
                todayRecords.add(record)
            }
        }

        return todayRecords
    }

    // Extensión para JSONArray para facilitar la búsqueda
    private fun JSONArray.any(predicate: (JSONObject) -> Boolean): Boolean {
        for (i in 0 until this.length()) {
            if (predicate(this.getJSONObject(i))) return true
        }
        return false
    }

    // Función para generar el texto de las estadísticas del día
    private fun getTodayStatisticsText(todayRecords: List<JSONObject>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Registros del día:\n\n")

        // Filtrar y agregar nacimientos
        val births = todayRecords.filter { it.has("Fecha de Nacimiento") }
        if (births.isNotEmpty()) {
            for (record in births) {
                stringBuilder.append(record.toString(4)).append("\n\n")
            }
        }

        return stringBuilder.toString().trim()
    }

    // Extensión para JSONArray para convertirlo en una lista de JSONObject
    private fun JSONArray.toList(): List<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until this.length()) {
            list.add(this.getJSONObject(i))
        }
        return list
    }
}