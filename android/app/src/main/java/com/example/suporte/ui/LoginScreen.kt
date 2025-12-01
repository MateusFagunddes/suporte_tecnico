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
import android.util.Patterns

// Função para validar formato de email
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun LoginScreen(vm: MainViewModel, onLogged: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("usuario") }
    var loading by remember { mutableStateOf(false) }
    var modeRegister by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Validar email quando o usuário digita (somente no modo registro)
    LaunchedEffect(email, modeRegister) {
        if (modeRegister && email.isNotEmpty()) {
            emailError = if (!isValidEmail(email)) {
                "Formato de email inválido"
            } else {
                null
            }
        } else {
            emailError = null
        }

        // Limpar mensagem de sucesso quando mudar de modo
        if (modeRegister) {
            successMessage = null
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {

        // Mensagem de sucesso após cadastro
        if (successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = successMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        if (modeRegister) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    unfocusedLabelColor = Color.Black
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
            onValueChange = {
                email = it
                // Limpar mensagem de sucesso quando usuário começar a digitar
                if (!modeRegister && successMessage != null) {
                    successMessage = null
                }
            },
            label = { Text(if (modeRegister) "Email" else "Email ou Usuário") },
            modifier = Modifier.fillMaxWidth(),
            isError = modeRegister && emailError != null,
            supportingText = {
                if (modeRegister && emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
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
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black
            )
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            // Validar se pode prosseguir
            if (modeRegister) {
                // Validações para cadastro
                if (nome.isBlank()) {
                    return@Button // Não prosseguir se nome vazio
                }
                if (email.isBlank()) {
                    return@Button // Não prosseguir se email vazio
                }
                if (!isValidEmail(email)) {
                    return@Button // Não prosseguir se email inválido
                }
                if (senha.isBlank()) {
                    return@Button // Não prosseguir se senha vazia
                }
            }

            loading = true
            if (modeRegister) {
                vm.registrar(nome, email, senha, role) { res ->
                    loading = false
                    if (res != null && res["status"] == "ok") {
                        // Cadastro bem-sucedido - limpar campos e voltar para login
                        nome = ""
                        email = ""
                        senha = ""
                        role = "usuario"
                        successMessage = "Usuário criado com sucesso! Faça login para continuar."
                        modeRegister = false // Voltar para tela de login

                        // Não fazer login automático - apenas redirecionar para login
                        // O usuário deve fazer login manualmente após cadastro
                    } else {
                        // erro no cadastro
                        loading = false
                    }
                }
            } else {
                vm.login(email, senha) { res ->
                    loading = false
                    if (res != null && res.isNotEmpty()) {
                        val id = (res["id"] as Double).toInt()
                        val nomeUsuario = res["nome"].toString()
                        val userRole = res["role"]?.toString() ?: "usuario"
                        val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)
                        prefs.edit()
                            .putInt("user_id", id)
                            .putString("user_email", email)
                            .putString("user_name", nomeUsuario)
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
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !loading && (!modeRegister || (nome.isNotBlank() && email.isNotBlank() && isValidEmail(email) && senha.isNotBlank()))
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (modeRegister) "Registrar" else "Entrar")
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { modeRegister = !modeRegister }) {
            Text(if (modeRegister) "Já tenho conta" else "Criar conta")
        }
    }
}
