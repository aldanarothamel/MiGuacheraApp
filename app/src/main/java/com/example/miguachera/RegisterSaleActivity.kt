package com.example.miguachera

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RegisterSaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_sale)

        val registerSaleButton: Button = findViewById(R.id.registerSaleButton)
        val salesContainer: LinearLayout = findViewById(R.id.salesContainer)

        // Leer y mostrar las ventas guardadas
        val sales = readSalesFromFile()
        displaySales(sales, salesContainer)

        registerSaleButton.setOnClickListener {
            showConfirmationDialog(sales, salesContainer)
        }
    }

    // Función para mostrar el cuadro de diálogo de confirmación
    private fun showConfirmationDialog(sales: JSONArray, salesContainer: LinearLayout) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar Registro de Venta")
        builder.setMessage("¿Está seguro de que desea registrar esta venta?")
        builder.setPositiveButton("Registrar") { dialog, _ ->
            registerSale(sales, salesContainer)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    // Función para registrar una nueva venta
    private fun registerSale(sales: JSONArray, salesContainer: LinearLayout) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val newSale = JSONObject().apply {
            put("fecha", currentDate)
        }
        sales.put(newSale)
        saveSalesToFile(sales)

        // Mostrar la nueva venta
        displaySales(sales, salesContainer)

        Toast.makeText(this, "Venta registrada exitosamente", Toast.LENGTH_SHORT).show()
    }

    // Función para leer las ventas desde el archivo JSON
    private fun readSalesFromFile(): JSONArray {
        val fileName = "sales.json"
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

    // Función para guardar las ventas en el archivo JSON
    private fun saveSalesToFile(sales: JSONArray) {
        val fileName = "sales.json"
        val file = File(filesDir, fileName)
        try {
            FileOutputStream(file).bufferedWriter().use { writer ->
                writer.write(sales.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Función para mostrar las ventas en el LinearLayout
    private fun displaySales(sales: JSONArray, container: LinearLayout) {
        container.removeAllViews()
        for (i in 0 until sales.length()) {
            val sale = sales.getJSONObject(i).getString("fecha")
            val textView = TextView(this).apply {
                text = "Venta realizada el día $sale"
                textSize = 16f
                setPadding(0, 0, 0, 16)
            }
            container.addView(textView)
        }
    }
}