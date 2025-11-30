package com.example.suporte.model

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String,
    val role: String = "usuario"
)
