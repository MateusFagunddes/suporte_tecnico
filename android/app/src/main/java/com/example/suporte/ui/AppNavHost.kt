package com.example.suporte.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.suporte.ui.theme.useThemeManager

@Composable
fun AppNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val vm = remember { MainViewModel(context) }
    var currentRoute by remember { mutableStateOf(if (vmIsLogged(context)) "main" else "login") }

    // Adicionar observação do tema para forçar recomposição
    val themeManager = useThemeManager()
    val isDarkTheme by themeManager.isDarkTheme.collectAsState()

    // Usar key para forçar recomposição quando o tema muda
    key(isDarkTheme) {
        NavHost(
            navController = nav,
            startDestination = if (vmIsLogged(context)) "main" else "login"
        ) {
        composable("login") {
            LoginScreen(
                vm = vm,
                onLogged = {
                    currentRoute = "main"
                    nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // Telas principais com sidebar
        composable("main") {
            currentRoute = "main"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                DashboardScreen(vm = vm)
            }
        }

        composable("chamados") {
            currentRoute = "chamados"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                ChamadosScreen(vm = vm)
            }
        }

        composable("novo_chamado") {
            currentRoute = "novo_chamado"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                NovoChamadoScreen(
                    vm = vm,
                    onChamadoCriado = {
                        // Navegar para chamados após criar
                        currentRoute = "chamados"
                        nav.navigate("chamados") {
                            popUpTo("main") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable("relatorios") {
            currentRoute = "relatorios"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                RelatoriosScreen()
            }
        }

        composable("configuracoes") {
            currentRoute = "configuracoes"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                ConfiguracoesScreen()
            }
        }

        composable("ajuda") {
            currentRoute = "ajuda"
            MainLayout(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    currentRoute = route
                    nav.navigate(route) {
                        popUpTo("main") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogoff = {
                    currentRoute = "login"
                    nav.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            ) {
                AjudaScreen()
            }
        }
    }
}
}

// simple check
fun vmIsLogged(context: android.content.Context): Boolean {
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    return prefs.getInt("user_id", -1) != -1
}
