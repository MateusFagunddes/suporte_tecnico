package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(vm: MainViewModel, onLogged: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var modeRegister by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        if (modeRegister) {
            OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            loading = true
            if (modeRegister) {
                vm.registrar(nome, email, senha) { res ->
                    loading = false
                    if (res != null && res["status"] == "ok") {
                        // salvar e prosseguir
                        val id = (res["id"] as Double).toInt()
                        val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit().putInt("user_id", id).putString("user_email", email).putString("user_name", nome).apply()
                        onLogged()
                    } else {
                        // erro simples
                        loading = false
                    }
                }
            } else {
                vm.login(email, senha) { res ->
                    loading = false
                    if (res != null && res.isNotEmpty()) {
                        val id = (res["id"] as Double).toInt()
                        val nome = res["nome"].toString()
                        val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit().putInt("user_id", id).putString("user_email", email).putString("user_name", nome).apply()
                        onLogged()
                    } else {
                        // login inválido
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (modeRegister) "Registrar" else "Entrar")
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { modeRegister = !modeRegister }) {
            Text(if (modeRegister) "Já tenho conta" else "Criar conta")
        }
    }
}
