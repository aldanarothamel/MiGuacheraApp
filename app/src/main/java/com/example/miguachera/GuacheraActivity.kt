package com.example.miguachera

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class GuacheraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guachera)

        // Botón para registrar nacimiento
        val registerBirthButton: Button = findViewById(R.id.registerBirthButton)
        registerBirthButton.setOnClickListener {
            val intent = Intent(this, RegisterBirthActivity::class.java)
            startActivity(intent)
        }

        // Botón para registrar fallecimiento
        val registerDeathButton: Button = findViewById(R.id.registerDeathButton)
        registerDeathButton.setOnClickListener {
            val intent = Intent(this, RegisterDeathActivity::class.java)
            startActivity(intent)
        }

        // Botón para registrar venta
        val registerSaleButton: Button = findViewById(R.id.registerSaleButton)
        registerSaleButton.setOnClickListener {
            val intent = Intent(this, RegisterSaleActivity::class.java)
            startActivity(intent)
        }

        // Botón para registrar tratamiento
        val registerTreatmentButton: Button = findViewById(R.id.registerTreatmentButton)
        registerTreatmentButton.setOnClickListener {
            val intent = Intent(this, RegisterTreatmentActivity::class.java)
            startActivity(intent)
        }

        // Botón para buscar ternero
        val searchCalfButton: Button = findViewById(R.id.searchCalfButton)
        searchCalfButton.setOnClickListener {
            val intent = Intent(this, SearchCalfActivity::class.java)
            startActivity(intent)
        }

        // Botón para ver estadísticas
        val statisticsButton: Button = findViewById(R.id.statisticsButton)
        statisticsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        // Botón para ir a la información de la temporada
        val seasonInfoButton: Button = findViewById(R.id.seasonInfoButton)
        seasonInfoButton.setOnClickListener {
            val intent = Intent(this, SeasonInfoActivity::class.java)
            startActivity(intent)
        }

        // Botón para notas
        val notesButton: Button = findViewById(R.id.notesButton)
        notesButton.setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            startActivity(intent)
        }

    }
}