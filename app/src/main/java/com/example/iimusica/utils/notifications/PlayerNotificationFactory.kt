package com.example.iimusica.utils.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.example.iimusica.MainActivity
import com.example.iimusica.R
import com.example.iimusica.utils.fetchers.getAlbumArtBitmap

@UnstableApi
object PlayerNotificationFactory {

    fun create(
        context: Context,
        notificationId: Int,
        channelId: String,
    ): PlayerNotificationManager {
        val appContext = context.applicationContext

        val notificationManager = PlayerNotificationManager.Builder(appContext, notificationId, channelId)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    val title = player.mediaMetadata.title ?: "Unknown Title"
                    return title
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    val artist = player.mediaMetadata.artist
                    return artist
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    // TODO test this!
                    return getAlbumArtBitmap(context, -1337L,
                        player.mediaMetadata.artworkUri.toString()
                    )

                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(appContext, MainActivity::class.java)
                    return PendingIntent.getActivity(
                        appContext,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            })
            .setSmallIconResourceId(R.drawable.default_image)
            .build()

        return notificationManager
    }
}
