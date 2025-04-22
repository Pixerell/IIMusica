package com.example.iimusica.player.notifications

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.example.iimusica.R
import com.example.iimusica.components.ux.getRepeatIconResId

const val CUSTOM_COMMAND_TOGGLE_REPEAT = "TOGGLE_REPEAT"
const val CUSTOM_COMMAND_SKIP_PREV = "SKIP_TO_PREV"
const val CUSTOM_COMMAND_PLAY_PAUSE = "PLAY_PAUSE"
const val CUSTOM_COMMAND_SKIP_NEXT = "SKIP_TO_NEXT"


enum class CustomAction(val actionId: String) {
    REPEAT(CUSTOM_COMMAND_TOGGLE_REPEAT),
    PREVIOUS(CUSTOM_COMMAND_SKIP_PREV),
    PLAY_PAUSE(CUSTOM_COMMAND_PLAY_PAUSE),
    NEXT(CUSTOM_COMMAND_SKIP_NEXT),
}

val orderedCustomActions = listOf(
    CustomAction.REPEAT,
    CustomAction.PREVIOUS,
    CustomAction.PLAY_PAUSE,
    CustomAction.NEXT
)

@OptIn(UnstableApi::class)
fun getCustomActionButton(action: CustomAction, iconResId: Int): CommandButton {
    require(iconResId != 0) { "iconResId is 0 for action: $action" }
    Log.d("notifz", "iconResId: $iconResId with action $action")

    // I didn't really find a better way to avoid this deprecation
    // Even recent google projects use this code sample
    @Suppress("DEPRECATION")
    return CommandButton.Builder()
        .setCustomIconResId(iconResId)
        .setDisplayName(action.name)
        .setSessionCommand(SessionCommand(action.actionId, Bundle()))
        .build()
}

fun getIconResIdForAction(action: CustomAction, exoPlayer : ExoPlayer): Int {
    return when (action) {
        CustomAction.REPEAT -> getRepeatIconResId(exoPlayer.repeatMode)
        CustomAction.PREVIOUS -> R.drawable.previco
        CustomAction.PLAY_PAUSE -> if (exoPlayer.playWhenReady) R.drawable.pauseico else R.drawable.playico
        CustomAction.NEXT -> R.drawable.nextico
    }
}


