package com.example.suporte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.suporte.ui.AppNavHost
import com.example.suporte.ui.theme.AppTheme
import com.example.suporte.ui.theme.ProvideThemeManager
import com.example.suporte.ui.theme.rememberThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeManager = rememberThemeManager(this)
            val isDarkTheme by themeManager.isDarkTheme.collectAsState()

            ProvideThemeManager(themeManager = themeManager) {
                AppTheme(isDarkTheme = isDarkTheme) {
                    AppNavHost()
                }
            }
        }
    }
}
