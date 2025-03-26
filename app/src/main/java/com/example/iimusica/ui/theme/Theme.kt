package com.example.iimusica.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier


@Composable
fun IIMusicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = remember(darkTheme) {
        if (!darkTheme) DarkAppColors else LightAppColors
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        ProvideTextStyle(Typography.bodyLarge) {
            val backgroundColor = appColors.background

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            ) {
                content()
            }
        }
    }
}
