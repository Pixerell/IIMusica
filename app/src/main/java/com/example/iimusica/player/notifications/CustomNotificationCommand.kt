package com.example.iimusica.player.notifications

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.example.iimusica.R
import com.example.iimusica.components.ux.getRepeatIconResId

const val CUSTOM_COMMAND_SKIP_PREV = "SKIP_TO_PREV"
const val CUSTOM_COMMAND_SKIP_NEXT = "SKIP_TO_NEXT"
const val CUSTOM_COMMAND_PLAY_PAUSE = "PLAY_PAUSE"
const val CUSTOM_COMMAND_TOGGLE_REPEAT = "TOGGLE_REPEAT"

// Static buttons
enum class CustomNotificationCommand(
    val actionId: String,
    val button: CommandButton
) {
    PREVIOUS(
        CUSTOM_COMMAND_SKIP_PREV,
        // I need to specify the icon id directly, or it will crash
        CommandButton.Builder()
            .setIconResId(R.drawable.previco)
            .setDisplayName("Previous")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_SKIP_PREV, Bundle()))
            .build()
    ),
    NEXT(
        CUSTOM_COMMAND_SKIP_NEXT,
        CommandButton.Builder()
            .setIconResId(R.drawable.nextico)
            .setDisplayName("Next")
            .setSessionCommand(SessionCommand(CUSTOM_COMMAND_SKIP_NEXT, Bundle()))
            .build()
    ),
}

// Dynamic Buttons
fun getPlayPauseCommandButton(isPlaying: Boolean): CommandButton {
    var icon = if (isPlaying) R.drawable.pauseico else R.drawable.playico
    return CommandButton.Builder()
        .setDisplayName("Repeat")
        .setIconResId(icon)
        .setSessionCommand(SessionCommand(CUSTOM_COMMAND_PLAY_PAUSE, Bundle()))
        .build()
}

fun getRepeatCommandButton(repeatMode: Int): CommandButton {
    return CommandButton.Builder()
        .setDisplayName("Repeat")
        .setIconResId(getRepeatIconResId(repeatMode))
        .setSessionCommand(SessionCommand(CUSTOM_COMMAND_TOGGLE_REPEAT, Bundle()))
        .build()
}

