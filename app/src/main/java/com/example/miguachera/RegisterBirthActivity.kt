package com.example.miguachera

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RegisterBirthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_birth)

        val saveButton: Button = findViewById(R.id.saveButton)
        val calfIdEditText: EditText = findViewById(R.id.calfId)
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

        saveButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()
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
                R.id.deadRadioButton -> "Sin Vida"
                else -> ""
            }

            if (calfId.isEmpty() || motherId.isEmpty() || birthDate.isEmpty() || sex.isEmpty() || generalState.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Leer los datos existentes
                val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                // Verificar si el ID del ternero ya existe
                if (isCalfIdExists(records, calfId)) {
                    Toast.makeText(this, "El ID del ternero ya existe. Por favor, ingrese un ID único.", Toast.LENGTH_SHORT).show()
                } else {
                    // Crear los datos nuevos
                    val newData = JSONObject().apply {
                        put("id", calfId)
                        put("Id de la Madre", motherId)
                        put("Fecha de Nacimiento", birthDate)
                        put("Sexo", sex)
                        put("Estado General al Nacer", generalState)
                    }
                    records.put(newData)

                    // Guardar los datos actualizados de nuevo en el archivo
                    if (RecordUtils.saveToFile("birth_records.json", filesDir, records)) {
                        Toast.makeText(this, "Nacimiento registrado exitosamente", Toast.LENGTH_SHORT).show()

                        // Limpiar los campos después de guardar
                        calfIdEditText.text.clear()
                        motherIdEditText.text.clear()
                        birthDateEditText.setText(currentDate)
                        sexGroup.clearCheck()
                        statusGroup.clearCheck()
                    } else {
                        Toast.makeText(this, "Error al registrar el nacimiento. Verifique los datos.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Función para verificar si el ID del ternero ya existe
    private fun isCalfIdExists(records: JSONArray, calfId: String): Boolean {
        for (i in 0 until records.length()) {
            val record = records.getJSONObject(i)
            if (record.getString("id").equals(calfId, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}