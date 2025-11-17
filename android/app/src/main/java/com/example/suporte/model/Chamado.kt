package com.example.suporte.model

data class Chamado(
    val id: Int,
    val titulo: String,
    val descricao: String,
    val status: String,
    val data_abertura: String,
    val usuario_id: Int?,
    val nome_usuario: String?
)
