package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import com.example.suporte.db.ChamadoEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(vm: MainViewModel, userRole: String = "usuario") {
    val chamados by vm.chamados.collectAsState()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

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
                title = if (userRole == "tecnico") "Total de Chamados" else "Meus Chamados",
                value = chamados.size.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Abertos",
                value = chamados.filter { it.status == "aberto" }.size.toString(),
                modifier = Modifier.weight(1f)
            )
            if (userRole == "tecnico") {
                StatCard(
                    title = "Em Andamento",
                    value = chamados.filter { it.status == "em andamento" }.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            if (userRole == "tecnico") "Chamados Recentes" else "Meus Chamados Recentes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chamados.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (userRole == "tecnico")
                                    "Nenhum chamado encontrado"
                                else
                                    "Você ainda não criou nenhum chamado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (userRole != "tecnico") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Use o menu para criar seu primeiro chamado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                items(chamados.take(5)) { chamado ->
                    if (userRole == "tecnico") {
                        ChamadoItemWithActions(
                            chamado = chamado,
                            isTecnico = true,
                            currentUserId = userId,
                            onStatusChange = { novoStatus ->
                                vm.atualizarStatus(chamado.id, novoStatus) { success ->
                                    // Status atualizado
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
                    } else {
                        ChamadoItemWithActions(
                            chamado = chamado,
                            isTecnico = false,
                            currentUserId = userId,
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
    }
}

@Composable
fun ChamadosScreen(vm: MainViewModel, userRole: String = "usuario") {
    val chamados by vm.chamados.collectAsState()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

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
                if (userRole == "tecnico") "Todos os Chamados" else "Meus Chamados",
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
            if (chamados.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (userRole == "tecnico")
                                    "Nenhum chamado encontrado"
                                else
                                    "Você ainda não criou nenhum chamado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (userRole != "tecnico") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Use o menu para criar seu primeiro chamado",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                items(chamados) { chamado ->
                    if (userRole == "tecnico") {
                        ChamadoItemWithActions(
                            chamado = chamado,
                            isTecnico = true,
                            currentUserId = userId,
                            onStatusChange = { novoStatus ->
                                vm.atualizarStatus(chamado.id, novoStatus) { success ->
                                    // Status atualizado
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
                    } else {
                        ChamadoItemWithActions(
                            chamado = chamado,
                            isTecnico = false,
                            currentUserId = userId,
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
    }
}

@Composable
fun NovoChamadoScreen(vm: MainViewModel, onChamadoCriado: () -> Unit = {}) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

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
                    vm.criar(titulo, descricao, if (userId != -1) userId else null)
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

// Função para formatar data no padrão brasileiro
fun formatarDataBR(dataString: String): String {
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
fun ChamadoItemWithActions(
    chamado: ChamadoEntity,
    isTecnico: Boolean = false,
    currentUserId: Int,
    onStatusChange: (String) -> Unit = {},
    onEdit: (String, String) -> Unit = { _, _ -> },
    onDelete: () -> Unit = {}
) {
    var showDropdown by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val statusOptions = listOf("aberto", "em andamento", "resolvido")

    // Verificar permissões
    val podeEditarExcluir = if (isTecnico) {
        true // Técnico pode editar/excluir qualquer chamado
    } else {
        chamado.usuario_id == currentUserId && chamado.status == "aberto" // Usuário só pode editar/excluir seus chamados abertos
    }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(chamado.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chamado.descricao, maxLines = 3)
            Spacer(Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Status: ${chamado.status}", style = MaterialTheme.typography.bodyMedium)

                if (isTecnico) {
                    // Botão para alterar status (somente para técnicos)
                    Box {
                        OutlinedButton(
                            onClick = { showDropdown = true },
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Text("Alterar Status")
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
                                    text = {
                                        Text(
                                            status,
                                            color = if (status == chamado.status)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        if (status != chamado.status) {
                                            onStatusChange(status)
                                        }
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Botões de ação (editar/excluir)
            if (podeEditarExcluir) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Editar", modifier = Modifier.padding(start = 4.dp))
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Excluir", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            if (isTecnico) {
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    if (chamado.usuario_id != null) {
                        Text("Usuário ID: ${chamado.usuario_id}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("Data: ${formatarDataBR(chamado.data_abertura)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    // Diálogo de Edição
    if (showEditDialog) {
        EditChamadoDialog(
            titulo = chamado.titulo,
            descricao = chamado.descricao,
            onDismiss = { showEditDialog = false },
            onSave = { novoTitulo, novaDescricao ->
                onEdit(novoTitulo, novaDescricao)
                showEditDialog = false
            }
        )
    }

    // Diálogo de Confirmação de Exclusão
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir este chamado? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun EditChamadoDialog(
    titulo: String,
    descricao: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var novoTitulo by remember { mutableStateOf(titulo) }
    var novaDescricao by remember { mutableStateOf(descricao) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Chamado") },
        text = {
            Column {
                OutlinedTextField(
                    value = novoTitulo,
                    onValueChange = { novoTitulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = novaDescricao,
                    onValueChange = { novaDescricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (novoTitulo.isNotBlank()) {
                        onSave(novoTitulo, novaDescricao)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
