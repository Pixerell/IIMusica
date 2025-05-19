package com.example.iimusica.types

data class QueueActions(
    val onSetQueue: () -> Unit,
    val onAddToQueue: () -> Unit,
    val onRemoveFromQueue: () -> Unit,
    val onClearQueue: () -> Unit,
    val onResetQueue: () -> Unit,
    val onSaveQueue: () -> Unit,
    val onGoToQueue: () -> Unit,
)
