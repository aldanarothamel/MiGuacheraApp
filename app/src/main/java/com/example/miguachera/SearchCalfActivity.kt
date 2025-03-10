package com.example.miguachera

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject

class SearchCalfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_calf)

        val calfIdEditText: EditText = findViewById(R.id.calfId)
        val motherIdEditText: EditText = findViewById(R.id.motherId)
        val searchByCalfIdButton: Button = findViewById(R.id.searchByCalfIdButton)
        val searchByMotherIdButton: Button = findViewById(R.id.searchByMotherIdButton)
        val searchResultTextView: TextView = findViewById(R.id.searchResult)
        val modifButton: Button = findViewById(R.id.modifButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        searchByCalfIdButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()

            if (calfId.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el identificador del ternero.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    // Leer los datos existentes
                    val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                    // Buscar el registro del ternero por ID
                    val calfData = getCalfDataById(records, calfId)

                    if (calfData != null) {
                        searchResultTextView.text = calfData.toString(4) // Formatear JSON para una mejor visualización
                        modifButton.visibility = Button.VISIBLE
                        deleteButton.visibility = Button.VISIBLE
                    } else {
                        searchResultTextView.text = "No se encontró un ternero con ese identificador."
                        modifButton.visibility = Button.GONE
                        deleteButton.visibility = Button.GONE
                    }
                } catch (e: Exception) {
                    Log.e("SearchCalfActivity", "Error buscando ternero: ${e.message}")
                    Toast.makeText(this, "Error al buscar el ternero. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        searchByMotherIdButton.setOnClickListener {
            val motherId = motherIdEditText.text.toString().trim()

            if (motherId.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el identificador de la madre.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    // Leer los datos existentes
                    val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                    // Buscar el ternero por ID de la madre
                    val calfData = getCalfDataByMotherId(records, motherId)

                    if (calfData != null) {
                        searchResultTextView.text = calfData.toString(4) // Formatear JSON para una mejor visualización
                        modifButton.visibility = Button.VISIBLE
                        deleteButton.visibility = Button.VISIBLE
                    } else {
                        searchResultTextView.text = "No se encontró un ternero con ese identificador de madre."
                        modifButton.visibility = Button.GONE
                        deleteButton.visibility = Button.GONE
                    }
                } catch (e: Exception) {
                    Log.e("SearchCalfActivity", "Error buscando ternero por ID de madre: ${e.message}")
                    Toast.makeText(this, "Error al buscar el ternero. Por favor, intente de nuevo.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        modifButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()
            if (calfId.isNotEmpty()) {
                val intent = Intent(this, ModifyCalfActivity::class.java).apply {
                    putExtra("CALF_ID", calfId)
                }
                startActivity(intent)
            }
        }

        deleteButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()
            if (calfId.isNotEmpty()) {
                showDeleteConfirmationDialog(calfId)
            }
        }
    }

    private fun getCalfDataById(records: JSONArray, calfId: String): JSONObject? {
        try {
            for (i in 0 until records.length()) {
                val record = records.getJSONObject(i)
                if (record.optString("id").equals(calfId, ignoreCase = true)) {
                    return record
                }
            }
        } catch (e: Exception) {
            Log.e("getCalfDataById", "Error al obtener datos del ternero: ${e.message}")
        }
        return null
    }

    private fun getCalfDataByMotherId(records: JSONArray, motherId: String): JSONObject? {
        try {
            for (i in 0 until records.length()) {
                val record = records.getJSONObject(i)
                if (record.optString("IdMadre").equals(motherId, ignoreCase = true)) {
                    return record
                }
            }
        } catch (e: Exception) {
            Log.e("getCalfDataByMotherId", "Error al obtener datos del ternero: ${e.message}")
        }
        return null
    }

    private fun showDeleteConfirmationDialog(calfId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Está seguro de que desea eliminar este ternero y todos sus registros asociados?")
        builder.setPositiveButton("Eliminar") { dialog, _ ->
            deleteCalfRecord(calfId)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteCalfRecord(calfId: String) {
        try {
            // Leer los datos existentes
            val records = RecordUtils.readFromFile("birth_records.json", filesDir)

            // Filtrar los datos para eliminar el registro del ternero
            val updatedRecords = JSONArray()
            for (i in 0 until records.length()) {
                val record = records.getJSONObject(i)
                if (!record.getString("id").equals(calfId, ignoreCase = true)) {
                    updatedRecords.put(record)
                }
            }

            // Guardar los datos actualizados de nuevo en el archivo
            if (RecordUtils.saveToFile("birth_records.json", filesDir, updatedRecords)) {
                Toast.makeText(this, "Ternero eliminado exitosamente", Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.calfId).text.clear()
                findViewById<TextView>(R.id.searchResult).text = ""
                findViewById<Button>(R.id.modifButton).visibility = Button.GONE
                findViewById<Button>(R.id.deleteButton).visibility = Button.GONE
            } else {
                Toast.makeText(this, "Error al eliminar el ternero. Intente de nuevo.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("deleteCalfRecord", "Error al eliminar el ternero: ${e.message}")
            Toast.makeText(this, "Error al eliminar el ternero. Intente de nuevo.", Toast.LENGTH_SHORT).show()
        }
    }
}