package com.example.suporte.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val vm = remember { MainViewModel(context) }

    NavHost(navController = nav, startDestination = if (vmIsLogged(context)) "main" else "login") {
        composable("login") { LoginScreen(vm = vm, onLogged = { nav.navigate("main"){popUpTo("login") { inclusive = true }} }) }
        composable("main") { MainScreen(vm = vm) }
    }
}

// simple check
fun vmIsLogged(context: android.content.Context): Boolean {
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    return prefs.getInt("user_id", -1) != -1
}
