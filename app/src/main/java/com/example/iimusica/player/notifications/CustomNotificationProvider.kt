package com.example.iimusica.player.notifications

import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import com.example.iimusica.player.PlaybackService.Companion.CHANNEL_ID
import com.example.iimusica.player.PlaybackService.Companion.NOTIFICATION_ID
import android.app.Notification
import android.content.Context
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList

@UnstableApi
class CustomNotificationProvider(
    private val context: Context,
    private val mediaLibrarySession: MediaLibrarySession
) : MediaNotification.Provider {

    override fun createNotification(
        mediaSession: MediaSession,
        mediaButtonPreferences: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val mediaItem =
            mediaSession.player.currentMediaItem ?: return MediaNotification(1, Notification())
        val notification =
            buildPlaybackNotification(context, mediaItem, mediaLibrarySession, CHANNEL_ID)
                .build()
        return MediaNotification(NOTIFICATION_ID, notification)
    }

    override fun handleCustomCommand(
        session: MediaSession,
        customAction: String,
        extras: Bundle
    ): Boolean {
        return false
    }
}
