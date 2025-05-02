package com.example.iimusica.components.ux


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random


@Composable
fun AudioVisualizerView(barCount: Int = 5, barWidth : Dp = 1.dp, barHeight : Float = 90f, barOpacity : Float = 1f, updateTime : Long = 50) {
    // Simulating audio data here
    val audioAmplitude = remember { mutableStateListOf<Float>() }
    // Update the list every frame (simulating audio data updates)
    LaunchedEffect(Unit) {
        while (true) {
            val newAmplitude = List(barCount) { Random.nextFloat() * Random.nextFloat() }
            audioAmplitude.clear()
            audioAmplitude.addAll(newAmplitude)
            delay(updateTime) // Update every 50ms
        }
    }
    AudioBarGraph(audioAmplitude = audioAmplitude, barWidth, barHeight, barOpacity)
}
