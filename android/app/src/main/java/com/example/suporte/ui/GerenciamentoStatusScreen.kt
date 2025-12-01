package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.suporte.model.Status
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciamentoStatusScreen(vm: MainViewModel = viewModel()) {
    var statusList by remember { mutableStateOf<List<Status>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var statusParaEditar by remember { mutableStateOf<Status?>(null) }
    var statusParaExcluir by remember { mutableStateOf<Status?>(null) }
    var mensagem by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    // Função para carregar status
    suspend fun carregarStatus() {
        try {
            isLoading = true
            statusList = vm.listarStatus()
            mensagem = "" // Limpar mensagem de erro se sucesso
        } catch (e: Exception) {
            mensagem = "Erro ao carregar status: ${e.message}\nTipo: ${e.javaClass.simpleName}"
            android.util.Log.e("GerenciamentoStatus", "Erro detalhado", e)
        } finally {
            isLoading = false
        }
    }

    // Carregar status na inicialização e quando refreshTrigger mudar
    LaunchedEffect(refreshTrigger) {
        carregarStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Status",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(
                    onClick = {
                        refreshTrigger++
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Atualizar")
                }

                FilledTonalButton(
                    onClick = { showCreateDialog = true }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Novo Status",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Novo Status")
                }
            }
        }

        if (mensagem.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = mensagem,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            LaunchedEffect(mensagem) {
                kotlinx.coroutines.delay(3000)
                mensagem = ""
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (statusList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Nenhum status encontrado",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Clique em 'Novo Status' para adicionar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(statusList) { status ->
                        StatusCard(
                            status = status,
                            onEdit = { statusParaEditar = it },
                            onDelete = { statusParaExcluir = it }
                        )
                    }
                }
            }
        }
    }

    // Dialog para criar/editar status
    if (showCreateDialog || statusParaEditar != null) {
        StatusDialog(
            status = statusParaEditar,
            onDismiss = {
                showCreateDialog = false
                statusParaEditar = null
            },
            onSave = { nome, ativo ->
                // Usar coroutines dentro do composable principal
                coroutineScope.launch {
                    try {
                        if (statusParaEditar != null) {
                            vm.atualizarStatus(statusParaEditar!!.id, nome, ativo)
                            mensagem = "Status atualizado com sucesso"
                        } else {
                            vm.criarStatus(nome, ativo)
                            mensagem = "Status criado com sucesso"
                        }
                        refreshTrigger++
                    } catch (e: Exception) {
                        mensagem = "Erro: ${e.message}"
                    } finally {
                        showCreateDialog = false
                        statusParaEditar = null
                    }
                }
            }
        )
    }

    // Dialog para confirmar exclusão
    statusParaExcluir?.let { status ->
        AlertDialog(
            onDismissRequest = { statusParaExcluir = null },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir o status '${status.nome}'?\n\nEsta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                vm.excluirStatus(status.id)
                                mensagem = "Status excluído com sucesso"
                                refreshTrigger++
                            } catch (e: Exception) {
                                mensagem = "Erro ao excluir: ${e.message}"
                            } finally {
                                statusParaExcluir = null
                            }
                        }
                    }
                ) {
                    Text("Excluir", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { statusParaExcluir = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StatusCard(
    status: Status,
    onEdit: (Status) -> Unit,
    onDelete: (Status) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Status: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (status.ativo) "Ativo" else "Inativo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (status.ativo)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row {
                IconButton(onClick = { onEdit(status) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onDelete(status) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun StatusDialog(
    status: Status?,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit
) {
    var nome by remember { mutableStateOf(status?.nome ?: "") }
    var ativo by remember { mutableStateOf(status?.ativo ?: true) }
    var nomeError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (status != null) "Editar Status" else "Novo Status")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        nomeError = it.isBlank()
                    },
                    label = { Text("Nome do Status") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nomeError,
                    supportingText = if (nomeError) {
                        { Text("Nome é obrigatório", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(
                        checked = ativo,
                        onCheckedChange = { ativo = it }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (ativo) "Ativo" else "Inativo",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nome.isNotBlank()) {
                        onSave(nome.trim(), ativo)
                    } else {
                        nomeError = true
                    }
                },
                enabled = nome.isNotBlank()
            ) {
                Text(if (status != null) "Atualizar" else "Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
