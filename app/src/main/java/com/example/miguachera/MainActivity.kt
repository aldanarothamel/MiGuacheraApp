package com.example.miguachera

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val requestCodePostNotifications = 1
    private val requestCodeScheduleExactAlarm = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el botón para ingresar a la guachera
        val enterGuacheraButton: Button = findViewById(R.id.enterGuacheraButton)
        enterGuacheraButton.setOnClickListener {
            val intent = Intent(this, GuacheraActivity::class.java)
            startActivity(intent)
        }

        // Solicitar permiso para notificaciones si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), requestCodePostNotifications)
            }
        }

        // Solicitar permiso para alarmas exactas si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if ((getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms().not()) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM), requestCodeScheduleExactAlarm)
            }
        }

        // Configurar AlarmManager para la notificación diaria
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, DailyNotificationReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        // Configurar el calendario para las 21:30 horas
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        // Establecer la alarma para que se repita todos los días a las 21:30 horas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, alarmIntent)
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent)
        }
    }
}