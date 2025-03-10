package com.example.miguachera

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RegisterTreatmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_treatment)

        // Inicializar todas las vistas
        val saveButton: Button = findViewById(R.id.saveButton)
        val treatmentDateEditText: EditText = findViewById(R.id.treatmentDate)
        val calfIdEditText: EditText = findViewById(R.id.calfId)
        val treatmentDetailsEditText: EditText = findViewById(R.id.treatmentDetails)

        // Configurar la fecha actual por defecto
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        treatmentDateEditText.setText(currentDate)

        // Configurar el DatePickerDialog
        treatmentDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                treatmentDateEditText.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }

        saveButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()
            val treatmentDate = treatmentDateEditText.text.toString().trim()
            val treatmentDetails = treatmentDetailsEditText.text.toString().trim()

            if (calfId.isEmpty() || treatmentDate.isEmpty() || treatmentDetails.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Leer los datos existentes
                val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                // Actualizar el registro del ternero con el nuevo tratamiento
                val updatedRecords = updateCalfTreatment(records, calfId, treatmentDate, treatmentDetails)

                // Guardar los datos actualizados de nuevo en el archivo
                if (RecordUtils.saveToFile("birth_records.json", filesDir, updatedRecords)) {
                    Toast.makeText(this, "Tratamiento registrado exitosamente", Toast.LENGTH_SHORT).show()

                    // Limpiar los campos después de guardar
                    calfIdEditText.text.clear()
                    treatmentDateEditText.setText(currentDate)
                    treatmentDetailsEditText.text.clear()
                } else {
                    Toast.makeText(this, "Error al registrar el tratamiento.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Función para actualizar el tratamiento del ternero
    private fun updateCalfTreatment(records: JSONArray, calfId: String, treatmentDate: String, treatmentDetails: String): JSONArray {
        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            if (record.getString("id").equals(calfId, ignoreCase = true)) {
                val treatments = record.optJSONArray("Tratamientos") ?: JSONArray().also { record.put("Tratamientos", it) }
                val newTreatment = JSONObject().apply {
                    put("Tratamiento", treatmentDetails)
                    put("Fecha de Aplicación", treatmentDate)
                }
                treatments.put(newTreatment)
                break
            }
        }
        return records
    }
}