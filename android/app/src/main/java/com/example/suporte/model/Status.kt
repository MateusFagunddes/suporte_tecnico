package com.example.suporte.model

import com.google.gson.annotations.SerializedName

data class Status(
    val id: Int,
    val nome: String,
    @SerializedName("ativo")
    val ativo: Boolean = true
)

data class CriarStatusRequest(
    val nome: String,
    val ativo: Boolean = true
)

data class AtualizarStatusRequest(
    val id: Int,
    val nome: String,
    val ativo: Boolean
)
