package com.example.suporte.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.graphics.Color
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

@Composable
fun LoginScreen(vm: MainViewModel, onLogged: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("usuario") }
    var loading by remember { mutableStateOf(false) }
    var modeRegister by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        if (modeRegister) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(Modifier.height(8.dp))

            // Role selection
            Text("Tipo de usuário:", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "usuario",
                        onClick = { role = "usuario" }
                    )
                    Text("Usuário")
                }
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "tecnico",
                        onClick = { role = "tecnico" }
                    )
                    Text("Técnico")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email ou Usuário") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            loading = true
            if (modeRegister) {
                vm.registrar(nome, email, senha, role) { res ->
                    loading = false
                    if (res != null && res["status"] == "ok") {
                        // salvar e prosseguir
                        val id = (res["id"] as Double).toInt()
                        val userRole = res["role"]?.toString() ?: "usuario"
                        val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", id)
                            .putString("user_email", email)
                            .putString("user_name", nome)
                            .putString("user_role", userRole)
                            .apply()

                        // Registrar token FCM
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                vm.salvarFcmToken(id, token)
                                Log.d("FCM", "Token registrado no login: $token")
                            }
                        }

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
                        val userRole = res["role"]?.toString() ?: "usuario"
                        val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", id)
                            .putString("user_email", email)
                            .putString("user_name", nome)
                            .putString("user_role", userRole)
                            .apply()

                        // Registrar token FCM
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                vm.salvarFcmToken(id, token)
                                Log.d("FCM", "Token registrado no login: $token")
                            }
                        }

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
