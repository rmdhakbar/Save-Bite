package com.bersamadapa.recylefood.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bersamadapa.recylefood.R

class NotificationHelper(private val context: Context) {

    private val channelId = "chat_notification_channel"
    private val channelName = "Chat Notifications"

    init {
        createNotificationChannel()
    }

    // Create Notification Channel for Android 8.0 and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH // High importance for heads-up notifications
            ).apply {
                description = "Channel for chat notifications"
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Show a heads-up notification
    fun showNotification(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title) // Title of the notification
            .setContentText(message) // Message displayed
            .setSmallIcon(R.drawable.logo_save_bite) // Your notification icon
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for heads-up
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Default sound, vibration, etc.
            .setAutoCancel(true) // Dismiss notification when clicked
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // Expandable message
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification) // Unique ID
    }
}
