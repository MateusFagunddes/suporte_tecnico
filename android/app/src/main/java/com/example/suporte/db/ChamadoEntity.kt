package com.example.suporte.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chamados")
data class ChamadoEntity(
    @PrimaryKey val id: Int,
    val titulo: String,
    val descricao: String,
    val status: String,
    val data_abertura: String,
    val usuario_id: Int?
)
