package com.example.miguachera

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RegisterDeathActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_death)

        val saveButton: Button = findViewById(R.id.saveButton)
        val deathDateEditText: EditText = findViewById(R.id.deathDate)
        val calfIdEditText: EditText = findViewById(R.id.calfId)

        // Configurar la fecha actual por defecto
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        deathDateEditText.setText(currentDate)

        // Configurar el DatePickerDialog
        deathDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                deathDateEditText.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }

        saveButton.setOnClickListener {
            val calfId = calfIdEditText.text.toString().trim()
            val deathDate = deathDateEditText.text.toString().trim()

            if (calfId.isEmpty() || deathDate.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                // Leer los datos existentes
                val records = RecordUtils.readFromFile("birth_records.json", filesDir)

                // Actualizar el registro del ternero
                val newData = JSONObject().apply {
                    put("Actualización de Estado", "Fallecido")
                    put("Fecha del Fallecimiento", deathDate)
                }
                val updatedRecords = RecordUtils.updateCalfRecord(records, calfId, newData)

                // Guardar los datos actualizados de nuevo en el archivo
                if (RecordUtils.saveToFile("birth_records.json", filesDir, updatedRecords)) {
                    Toast.makeText(this, "Fallecimiento registrado exitosamente", Toast.LENGTH_SHORT).show()

                    // Limpiar los campos después de guardar
                    calfIdEditText.text.clear()
                    deathDateEditText.setText(currentDate)
                } else {
                    Toast.makeText(this, "Error al registrar el fallecimiento.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}