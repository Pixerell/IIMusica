package com.example.iimusica.utils

import androidx.compose.runtime.compositionLocalOf

val LocalDismissSearch = compositionLocalOf<() -> Unit> { {} }