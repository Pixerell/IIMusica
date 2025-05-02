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
    val accentGradient: Brush,
    val active : Color,
    val activeDarker : Color,
    val activeStart: Color,
    val activeEnd: Color,
    val activeGradient: Brush,
)

// Dark Theme Colors
val DarkAppColors = AppColors(
    background = Color(0xFF030310),
    backgroundDarker = Color.Black,
    font = Color.White,
    secondaryFont = Color.Gray,
    icon = Color.White,
    accentStart = Color(0xFF0B1045),
    accentEnd = Color(0xFF4B134B),
    accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4B134B), Color(0xFF0B1045))
    ),
    active = Color(0xFFF05CFF),
    activeDarker = Color(0xFF55309F),
    activeStart = Color(0xFFF05CFF),
    activeEnd = Color(0xFFFF287E),
    activeGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF05CFF), Color(0xFFFF287E))
    )
)

val LightAppColors = AppColors(
    background = Color.White,
    backgroundDarker = Color(0xFFD1DFFF),
    font = Color.Black,
    secondaryFont = Color.DarkGray,
    icon = Color.Black,
    accentStart = Color(0xFF9D73FF),
    accentEnd = Color(0xFF7EFFEA),
    accentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF9D73FF), Color(0xFF7EFFEA))
    ),
    active = Color(0xFF335EC9),
    activeDarker = Color(0xFFA5F6FF),
    activeStart = Color(0xFF335EC9),
    activeEnd = Color(0xFF171B67),
    activeGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF335EC9), Color(0xFF171B67))
    )

)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }
