package com.example.miguachera

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ModifyCalfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_calf)

        val calfId = intent.getStringExtra("CALF_ID")
        val calfIdEditText: EditText = findViewById(R.id.calfId)
        val saveButton: Button = findViewById(R.id.saveButton)
        val motherIdEditText: EditText = findViewById(R.id.motherId)
        val birthDateEditText: EditText = findViewById(R.id.birthDate)
        val sexGroup: RadioGroup = findViewById(R.id.sexGroup)
        val statusGroup: RadioGroup = findViewById(R.id.statusGroup)

        // Configurar la fecha actual por defecto
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        birthDateEditText.setText(currentDate)

        // Configurar el DatePickerDialog
        birthDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                birthDateEditText.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }

        // Cargar los datos existentes del ternero
        if (!calfId.isNullOrEmpty()) {
            val records = RecordUtils.readFromFile("birth_records.json", filesDir)
            val calfData = getCalfData(records, calfId)

            if (calfData != null) {
                // Mostrar el ID del ternero
                calfIdEditText.setText(calfId)
                calfIdEditText.isEnabled = false  // Deshabilitar la edición del ID del ternero

                // Cargar los datos en los campos correspondientes
                motherIdEditText.setText(calfData.optString("Id de la Madre", ""))
                birthDateEditText.setText(calfData.optString("Fecha de Nacimiento", ""))

                val sex = calfData.optString("Sexo", "")
                when (sex) {
                    "Macho" -> sexGroup.check(R.id.maleRadioButton)
                    "Hembra" -> sexGroup.check(R.id.femaleRadioButton)
                    "No visible" -> sexGroup.check(R.id.nonRadioButton)
                }

                val generalState = calfData.optString("Estado General al Nacer", "")
                when (generalState) {
                    "Sano" -> statusGroup.check(R.id.healthyRadioButton)
                    "Enfermo" -> statusGroup.check(R.id.sickRadioButton)
                    "Muerto" -> statusGroup.check(R.id.deadRadioButton)
                }
            }
        }

        saveButton.setOnClickListener {
            val motherId = motherIdEditText.text.toString().trim()
            val birthDate = birthDateEditText.text.toString().trim()

            // Obtener el sexo seleccionado
            val selectedSexId = sexGroup.checkedRadioButtonId
            val sex = when (selectedSexId) {
                R.id.maleRadioButton -> "Macho"
                R.id.femaleRadioButton -> "Hembra"
                R.id.nonRadioButton -> "No visible"
                else -> ""
            }

            // Obtener el estado general seleccionado
            val selectedStatusId = statusGroup.checkedRadioButtonId
            val generalState = when (selectedStatusId) {
                R.id.healthyRadioButton -> "Sano"
                R.id.sickRadioButton -> "Enfermo"
                R.id.deadRadioButton -> "Muerto"
                else -> ""
            }

            if (calfId.isNullOrEmpty() || motherId.isEmpty() || birthDate.isEmpty() || sex.isEmpty() || generalState.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Leer los datos existentes
                val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                // Crear los datos nuevos
                val newData = JSONObject().apply {
                    put("Id de la Madre", motherId)
                    put("Fecha de Nacimiento", birthDate)
                    put("Sexo", sex)
                    put("Estado General al Nacer", generalState)
                }
                val updatedRecords = RecordUtils.updateCalfRecord(records, calfId, newData)

                // Guardar los datos actualizados de nuevo en el archivo
                if (RecordUtils.saveToFile("birth_records.json", filesDir, updatedRecords)) {
                    Toast.makeText(this, "Datos del ternero actualizados exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar los datos del ternero.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCalfData(records: JSONArray, calfId: String): JSONObject? {
        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            if (record.getString("id").equals(calfId, ignoreCase = true)) {
                return record
            }
        }
        return null
    }
}