package com.example.iimusica.components.ux


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun AudioVisualizerView() {
    // Simulating audio data here
    val audioAmplitude = remember { mutableStateListOf<Float>() }

    // Update the list every frame (simulating audio data updates)
    LaunchedEffect(Unit) {
        while (true) {
            val newAmplitude = List(5) { Random.nextFloat() * Random.nextFloat() }
            audioAmplitude.clear()
            audioAmplitude.addAll(newAmplitude)
            delay(50) // Update every 50ms
        }
    }

    AudioBarGraph(audioAmplitude = audioAmplitude)
}
