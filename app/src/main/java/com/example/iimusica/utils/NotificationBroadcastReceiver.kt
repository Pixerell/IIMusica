package com.example.iimusica.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.iimusica.screens.PlaybackController


class NotificationBroadcastReceiver : BroadcastReceiver() {
    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "Notification action clicked!")
        when (intent.action) {
            // Action for toggling play/pause
            null -> {
                PlaybackController.togglePlayPause()
                Toast.makeText(context, "Toggling play/pause", Toast.LENGTH_SHORT).show()
            }
            // Action for sending a message
            "SEND_MESSAGE" -> {
                Log.d("NotificationReceiver", "Sending message!")
                Toast.makeText(context, "This is a message from the notification!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d("NotificationReceiver", "Unknown action!")
            }
        }
        Toast.makeText(context, "Notification action clicked!", Toast.LENGTH_SHORT).show()
    }
}
