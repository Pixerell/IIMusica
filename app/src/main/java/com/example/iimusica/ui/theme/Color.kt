package com.example.iimusica.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val background: Color,
    val backgroundDarker: Color,
    val font: Color,
    val secondaryFont: Color,
    val icon: Color,
    val accentStart: Color,
    val accentEnd: Color,
    val accentGradient: Brush
)

// Dark Theme Colors
val DarkAppColors = AppColors(
    background = Color(0xFF030310),
    backgroundDarker = Color.Black,
    font = Color.White,
    secondaryFont = Color.Gray,
    icon = Color.White,
    accentStart = Color(0xFF0B1045),
    accentEnd = Color(0xFF5B245A),
    accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0B1045), Color(0xFF5B245A))
    )
)

val LightAppColors = AppColors(
    background = Color.White,
    backgroundDarker = Color(0xFFFFFFFF),
    font = Color.Black,
    secondaryFont = Color.DarkGray,
    icon = Color.Black,
    accentStart = Color(0xFF9D73FF),
    accentEnd = Color(0xFF7EFFEA),
    accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF9D73FF), Color(0xFF7EFFEA))
    )
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }
