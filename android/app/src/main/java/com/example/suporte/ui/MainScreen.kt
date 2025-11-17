package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.suporte.db.ChamadoEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel) {
    val chamados by vm.chamados.collectAsState()
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
    val userId = prefs.getInt("user_id", -1)

    LaunchedEffect(Unit) { vm.carregar() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Suporte Técnico") }) },
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
                Text("Chamados", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(chamados) { chamado -> ChamadoItem(chamado) }
                }
            }
        }
    )
}

@Composable
fun ChamadoItem(chamado: ChamadoEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(chamado.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(chamado.descricao, maxLines = 3)
            Spacer(Modifier.height(6.dp))
            Text("Status: ${'$'}{chamado.status}")
        }
    }
}
