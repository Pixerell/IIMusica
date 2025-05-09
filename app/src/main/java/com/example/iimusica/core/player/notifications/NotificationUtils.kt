package com.example.iimusica.core.player.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String,
    channelDescription: String
) {
    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = channelDescription
    }
    val notificationManager =
        context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}
