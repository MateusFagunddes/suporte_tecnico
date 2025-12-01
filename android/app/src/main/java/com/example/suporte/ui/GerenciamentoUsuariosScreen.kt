package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.suporte.model.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GerenciamentoUsuariosScreen(
    vm: MainViewModel,
    onNavigateBack: () -> Unit = {}
) {
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var usuarioEditando by remember { mutableStateOf<Usuario?>(null) }
    var mensagemError by remember { mutableStateOf<String?>(null) }
    var mensagemSucesso by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Função para carregar usuários
    fun carregarUsuarios() {
        scope.launch {
            loading = true
            try {
                usuarios = vm.listarUsuarios()
                mensagemError = null
            } catch (e: Exception) {
                mensagemError = "Erro ao carregar usuários: ${e.message}"
            } finally {
                loading = false
            }
        }
    }

    // Carregar usuários ao iniciar
    LaunchedEffect(Unit) {
        carregarUsuarios()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Usuários") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { carregarUsuarios() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    usuarioEditando = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Usuário")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Mensagens de feedback
            mensagemError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            mensagemSucesso?.let { sucesso ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = sucesso,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Indicador de loading
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de usuários
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(usuarios) { usuario ->
                    UsuarioCard(
                        usuario = usuario,
                        onEdit = {
                            usuarioEditando = usuario
                            showDialog = true
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    val response = vm.excluirUsuario(usuario.id)
                                    if (response["status"] == "ok") {
                                        mensagemSucesso = "Usuário excluído com sucesso!"
                                        mensagemError = null
                                        carregarUsuarios()
                                    } else {
                                        mensagemError = response["message"]?.toString() ?: "Erro ao excluir usuário"
                                        mensagemSucesso = null
                                    }
                                } catch (e: Exception) {
                                    mensagemError = "Erro ao excluir usuário: ${e.message}"
                                    mensagemSucesso = null
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Dialog de criação/edição
    if (showDialog) {
        UsuarioFormDialog(
            usuario = usuarioEditando,
            onDismiss = {
                showDialog = false
                usuarioEditando = null
            },
            onSave = { nome, email, senha, role ->
                scope.launch {
                    try {
                        val response = if (usuarioEditando == null) {
                            vm.criarUsuario(nome, email, senha, role)
                        } else {
                            vm.editarUsuario(usuarioEditando!!.id, nome, email, senha, role)
                        }

                        if (response["status"] == "ok") {
                            mensagemSucesso = if (usuarioEditando == null) {
                                "Usuário criado com sucesso!"
                            } else {
                                "Usuário atualizado com sucesso!"
                            }
                            mensagemError = null
                            showDialog = false
                            usuarioEditando = null
                            carregarUsuarios()
                        } else {
                            mensagemError = response["message"]?.toString() ?: "Erro ao salvar usuário"
                            mensagemSucesso = null
                        }
                    } catch (e: Exception) {
                        mensagemError = "Erro ao salvar usuário: ${e.message}"
                        mensagemSucesso = null
                    }
                }
            }
        )
    }

    // Limpar mensagens após um tempo
    LaunchedEffect(mensagemSucesso, mensagemError) {
        if (mensagemSucesso != null) {
            kotlinx.coroutines.delay(3000)
            mensagemSucesso = null
        }
        if (mensagemError != null) {
            kotlinx.coroutines.delay(5000)
            mensagemError = null
        }
    }
}

@Composable
fun UsuarioCard(
    usuario: Usuario,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (usuario.role == "tecnico") Icons.Default.Build else Icons.Default.Person,
                        contentDescription = usuario.role,
                        tint = if (usuario.role == "tecnico") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = usuario.nome,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = usuario.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (usuario.role == "tecnico") "Técnico" else "Usuário",
                style = MaterialTheme.typography.labelSmall,
                color = if (usuario.role == "tecnico") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun UsuarioFormDialog(
    usuario: Usuario? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var nome by remember { mutableStateOf(usuario?.nome ?: "") }
    var email by remember { mutableStateOf(usuario?.email ?: "") }
    var senha by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(usuario?.role ?: "usuario") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (usuario == null) "Novo Usuário" else "Editar Usuário")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text(if (usuario == null) "Senha" else "Nova Senha (deixe vazio para manter)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                Text("Tipo de usuário:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == "usuario",
                            onClick = { role = "usuario" }
                        )
                        Text("Usuário")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == "tecnico",
                            onClick = { role = "tecnico" }
                        )
                        Text("Técnico")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nome.isNotBlank() && email.isNotBlank() && (usuario != null || senha.isNotBlank())) {
                        onSave(nome, email, senha, role)
                    }
                },
                enabled = nome.isNotBlank() && email.isNotBlank() && (usuario != null || senha.isNotBlank())
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
