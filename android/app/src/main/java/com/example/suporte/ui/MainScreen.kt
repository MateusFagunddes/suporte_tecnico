package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.suporte.db.ChamadoEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel) {
    val chamados by vm.chamados.collectAsState()
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)
    val userName = prefs.getString("user_name", "Usuário") ?: "Usuário"
    val userRole = prefs.getString("user_role", "usuario") ?: "usuario"

    LaunchedEffect(Unit) { vm.carregar() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Suporte Técnico")
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "$userName (${if (userRole == "tecnico") "Técnico" else "Usuário"})",
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (userRole == "tecnico") {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Técnico",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { vm.carregar() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(12.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = descricao, onValueChange = { descricao = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    if (titulo.isNotBlank()) {
                        vm.criar(titulo, descricao, if (userId != -1) userId else null)
                        titulo = ""
                        descricao = ""
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Abrir chamado")
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chamados", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = { vm.carregar() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar chamados"
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(chamados) { chamado ->
                        ChamadoItemWithActions(
                            chamado = chamado,
                            isTecnico = userRole == "tecnico",
                            currentUserId = userId,
                            onStatusChange = { novoStatus ->
                                vm.atualizarStatus(chamado.id, novoStatus) { success ->
                                    if (success) {
                                        // Status atualizado com sucesso
                                    }
                                }
                            },
                            onEdit = { titulo, descricao ->
                                vm.editarChamado(chamado.id, titulo, descricao) { success ->
                                    // Chamado editado
                                }
                            },
                            onDelete = {
                                vm.excluirChamado(chamado.id) { success ->
                                    // Chamado excluído
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

// Função para formatar data no padrão brasileiro
fun formatarData(dataString: String): String {
    return try {
        // Formato de entrada do servidor (assumindo ISO ou MySQL datetime)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        val date = inputFormat.parse(dataString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        // Se não conseguir formatar, retorna a string original
        dataString
    }
}

@Composable
fun ChamadoItem(chamado: ChamadoEntity, isTecnico: Boolean = false) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(chamado.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chamado.descricao, maxLines = 3)
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Status: ${chamado.status}")
                if (isTecnico && chamado.usuario_id != null) {
                    Text("ID: ${chamado.usuario_id}", style = MaterialTheme.typography.bodySmall)
                }
            }
            if (isTecnico) {
                Spacer(Modifier.height(4.dp))
                Text("Data: ${formatarData(chamado.data_abertura)}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
