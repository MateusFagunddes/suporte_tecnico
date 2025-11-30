package com.example.suporte.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun AppTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) {
        DarkColors
    } else {
        LightColors
    }

    // Usar key para forçar recomposição quando o tema muda
    key(isDarkTheme) {
        MaterialTheme(colorScheme = colorScheme) {
            content()
        }
    }
}
