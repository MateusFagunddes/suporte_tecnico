package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.suporte.db.ChamadoEntity

@Composable
fun DashboardScreen(vm: MainViewModel, userRole: String = "usuario") {
    val chamados by vm.chamados.collectAsState()

    LaunchedEffect(Unit) { vm.carregar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabeçalho com botão de refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { vm.carregar() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cards de resumo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Total de Chamados",
                value = chamados.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Abertos",
                value = chamados.filter { it.status == "aberto" }.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Chamados Recentes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chamados.take(5)) { chamado ->
                if (userRole == "tecnico") {
                    ChamadoItemWithStatus(
                        chamado = chamado,
                        isTecnico = true,
                        onStatusChange = { novoStatus ->
                            vm.atualizarStatus(chamado.id, novoStatus) { success ->
                                // Status atualizado
                            }
                        }
                    )
                } else {
                    ChamadoItem(chamado)
                }
            }
        }
    }
}

@Composable
fun ChamadosScreen(vm: MainViewModel, userRole: String = "usuario") {
    val chamados by vm.chamados.collectAsState()

    LaunchedEffect(Unit) { vm.carregar() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabeçalho com botão de refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Todos os Chamados",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { vm.carregar() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chamados) { chamado ->
                if (userRole == "tecnico") {
                    ChamadoItemWithStatus(
                        chamado = chamado,
                        isTecnico = true,
                        onStatusChange = { novoStatus ->
                            vm.atualizarStatus(chamado.id, novoStatus) { success ->
                                // Status atualizado
                            }
                        }
                    )
                } else {
                    ChamadoItem(chamado)
                }
            }
        }
    }
}

@Composable
fun NovoChamadoScreen(vm: MainViewModel, onChamadoCriado: () -> Unit = {}) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Abrir Novo Chamado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título do Chamado") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (titulo.isNotBlank()) {
                    vm.criar(titulo, descricao, null)
                    titulo = ""
                    descricao = ""
                    onChamadoCriado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abrir Chamado")
        }
    }
}

@Composable
fun RelatoriosScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Relatórios",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Funcionalidade em desenvolvimento...")
    }
}

@Composable
fun ConfiguracoesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Configurações",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Funcionalidade em desenvolvimento...")
    }
}

@Composable
fun AjudaScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Ajuda",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Funcionalidade em desenvolvimento...")
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ChamadoItem(chamado: ChamadoEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(chamado.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chamado.descricao, maxLines = 3)
            Spacer(Modifier.height(6.dp))
            Text("Status: ${chamado.status}")
        }
    }
}

@Composable
fun ChamadoItemWithStatus(
    chamado: ChamadoEntity,
    isTecnico: Boolean = false,
    onStatusChange: (String) -> Unit = {}
) {
    var showDropdown by remember { mutableStateOf(false) }
    val statusOptions = listOf("aberto", "em andamento", "resolvido")

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(chamado.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chamado.descricao, maxLines = 3)
            Spacer(Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (isTecnico) {
                    // Dropdown para alterar status (somente para técnicos)
                    Box {
                        OutlinedButton(
                            onClick = { showDropdown = true },
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text("Status: ${chamado.status}")
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Alterar status",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            statusOptions.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        onStatusChange(status)
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text("Status: ${chamado.status}")
                }

                if (isTecnico && chamado.usuario_id != null) {
                    Text("ID: ${chamado.usuario_id}", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (isTecnico) {
                Spacer(Modifier.height(4.dp))
                Text("Data: ${chamado.data_abertura}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
