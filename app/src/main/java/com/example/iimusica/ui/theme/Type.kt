package com.example.iimusica.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.iimusica.R


val Tektur = FontFamily(
    Font(R.font.tektur_regular, FontWeight.Normal),
    Font(R.font.tektur_bold, FontWeight.Bold)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Tektur,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Tektur,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Tektur,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Tektur,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Tektur,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
)
