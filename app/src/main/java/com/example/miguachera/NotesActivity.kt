package com.example.miguachera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val noteEditText: EditText = findViewById(R.id.noteEditText)
        val saveNoteButton: Button = findViewById(R.id.saveNoteButton)
        val notesContainer: LinearLayout = findViewById(R.id.notesContainer)

        // Leer y mostrar las notas guardadas
        val notes = readNotesFromFile()
        displayNotes(notes, notesContainer)

        saveNoteButton.setOnClickListener {
            val noteText = noteEditText.text.toString().trim()

            if (noteText.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese una nota.", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar la nueva nota
                val newNote = JSONObject().apply {
                    put("note", noteText)
                }
                notes.put(newNote)
                saveNotesToFile(notes)

                // Mostrar la nueva nota
                displayNotes(notes, notesContainer)

                // Limpiar el campo de texto
                noteEditText.text.clear()
            }
        }
    }

    // Función para leer las notas desde el archivo JSON
    private fun readNotesFromFile(): JSONArray {
        val fileName = "notes.json"
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

    // Función para guardar las notas en el archivo JSON
    private fun saveNotesToFile(notes: JSONArray) {
        val fileName = "notes.json"
        val file = File(filesDir, fileName)
        try {
            FileOutputStream(file).bufferedWriter().use { writer ->
                writer.write(notes.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Función para mostrar las notas en el LinearLayout
    private fun displayNotes(notes: JSONArray, container: LinearLayout) {
        container.removeAllViews()
        for (i in 0 until notes.length()) {
            val note = notes.getJSONObject(i).getString("note")
            val noteLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 0, 0, 16)
            }
            val textView = TextView(this).apply {
                text = note
                textSize = 23f
                setPadding(0, 0, 16, 0)
            }
            val deleteButton = Button(this).apply {
                text = "Eliminar"
                setOnClickListener {
                    deleteNoteAtIndex(i)
                }
            }
            noteLayout.addView(textView)
            noteLayout.addView(deleteButton)
            container.addView(noteLayout)
        }
    }

    // Función para eliminar una nota en un índice específico
    private fun deleteNoteAtIndex(index: Int) {
        val notes = readNotesFromFile()
        if (index >= 0 && index < notes.length()) {
            notes.remove(index)
            saveNotesToFile(notes)
            displayNotes(notes, findViewById(R.id.notesContainer))
            Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show()
        }
    }
}