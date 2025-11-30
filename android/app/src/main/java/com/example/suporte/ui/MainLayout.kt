package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.suporte.ui.theme.useThemeManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogoff: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val themeManager = useThemeManager()
    val isDarkTheme by themeManager.isDarkTheme.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    onNavigate(route)
                    scope.launch {
                        drawerState.close()
                    }
                },
                onLogoff = {
                    // Limpar sessão
                    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    onLogoff()
                    scope.launch {
                        drawerState.close()
                    }
                },
                isDarkTheme = isDarkTheme,
                onThemeToggle = {
                    themeManager.toggleTheme()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(getPageTitle(currentRoute))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        drawerState.open()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content()
            }
        }
    }
}

private fun getPageTitle(route: String): String {
    return when (route) {
        "main" -> "Dashboard"
        "chamados" -> "Chamados"
        "novo_chamado" -> "Novo Chamado"
        "relatorios" -> "Relatórios"
        "configuracoes" -> "Configurações"
        "ajuda" -> "Ajuda"
        else -> "Suporte Técnico"
    }
}
