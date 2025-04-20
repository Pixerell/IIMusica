package com.example.iimusica.player.notifications

import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.example.iimusica.R

const val CUSTOM_COMMAND_SKIP_PREV = "SKIP_TO_PREV"
const val CUSTOM_COMMAND_SKIP_NEXT = "SKIP_TO_NEXT"

enum class CustomNotificationCommand(
    val actionId: String,
    val button: CommandButton
) {
    PREVIOUS(
        CUSTOM_COMMAND_SKIP_PREV,
        // we must specify the icon id directly, or it will crash
        CommandButton.Builder()
            .setIconResId(R.drawable.default_image)
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
    )
}
