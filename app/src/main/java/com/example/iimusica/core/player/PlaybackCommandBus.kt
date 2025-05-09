package com.example.iimusica.core.player


import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object PlaybackCommandBus {
    const val BUS_NEXT = "NEXT"
    const val BUS_PREV = "PREV"
    const val BUS_TOGGLE_REPEAT = "TOGGLE_REPEAT"

    private val _commands = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val commands: SharedFlow<String> = _commands

    fun sendCommand(command: String) {
        _commands.tryEmit(command)
    }
}

