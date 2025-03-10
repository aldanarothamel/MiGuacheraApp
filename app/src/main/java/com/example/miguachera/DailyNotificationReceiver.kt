package com.example.miguachera

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Crear la notificación
        createNotification(context)
    }

    private fun createNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear un canal de notificación para Android Oreo y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "daily_notification_channel",
                "Notificaciones Diarias",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para notificaciones diarias"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Configurar la acción de la notificación
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, "daily_notification_channel")
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un ícono de notificación en tu proyecto
            .setContentTitle("Buenas Noches Lalo")
            .setContentText("¡No olvides compartir tus registros del dia!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }
}