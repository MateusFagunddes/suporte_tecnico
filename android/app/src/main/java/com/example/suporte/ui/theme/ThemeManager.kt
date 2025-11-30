package com.example.suporte.ui.theme

import android.content.Context
import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(
        prefs.getBoolean("is_dark_theme", false)
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        prefs.edit().putBoolean("is_dark_theme", newTheme).apply()
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }
}

// Context para o ThemeManager
private val LocalThemeManager = compositionLocalOf<ThemeManager?> { null }

@Composable
fun ProvideThemeManager(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        content()
    }
}

@Composable
fun useThemeManager(): ThemeManager {
    return LocalThemeManager.current ?: error("ThemeManager not provided")
}

@Composable
fun rememberThemeManager(context: Context): ThemeManager {
    return remember { ThemeManager(context) }
}
