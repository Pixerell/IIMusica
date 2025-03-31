package com.example.iimusica.components

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(UnstableApi::class)
@Composable
fun AudioVisualizerView() {
    // Simulating audio data here (replace with actual audio data from AudioRecord)
    val audioAmplitude = remember { mutableStateListOf<Float>() }

    // Update the list every frame (simulating audio data updates)
    LaunchedEffect(Unit) {
        while (true) {
            val newAmplitude = List(5) { Random.nextFloat() * Random.nextFloat()}
            audioAmplitude.clear()
            audioAmplitude.addAll(newAmplitude)
            delay(50) // Update every 50ms
        }
    }

    AudioBarGraph(audioAmplitude = audioAmplitude)
}
