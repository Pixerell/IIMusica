package com.example.iimusica.player.notifications


import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaStyleNotificationHelper.MediaStyle
import com.example.iimusica.R
import com.example.iimusica.utils.fetchers.SKIP_CHECK_CODE
import com.example.iimusica.utils.fetchers.getAlbumArtBitmap


@OptIn(UnstableApi::class)
fun buildPlaybackNotification(
    context: Context,
    mediaItem: MediaItem,
    mediaLibrarySession: MediaLibrarySession,
    channelId: String
): NotificationCompat.Builder {

    return NotificationCompat.Builder(context, channelId)
        .setContentTitle(mediaItem.mediaMetadata.title)
        .setContentText(mediaItem.mediaMetadata.artist)
        .setSmallIcon(R.drawable.applogosimple)
        .setLargeIcon(getAlbumArtBitmap(context, SKIP_CHECK_CODE, mediaItem.mediaId))
        .setStyle(
            MediaStyle(mediaLibrarySession))
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setOnlyAlertOnce(true)

}
