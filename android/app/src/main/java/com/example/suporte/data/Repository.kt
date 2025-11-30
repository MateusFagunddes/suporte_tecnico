package com.example.suporte.data

import android.content.Context
import com.example.suporte.db.AppDatabase
import com.example.suporte.db.ChamadoEntity
import com.example.suporte.di.Network
import com.example.suporte.di.ApiService

class Repository(context: Context) {
    private val api = Network.retrofit.create(ApiService::class.java)
    private val db = AppDatabase.getInstance(context)
    private val dao = db.chamadoDao()

    // fetch from server, update local DB
    suspend fun syncFromServer() {
        val list = api.listarChamados()
        val entities = list.map { ChamadoEntity(it.id, it.titulo, it.descricao, it.status, it.data_abertura, it.usuario_id) }
        dao.insertAll(entities)
    }

    suspend fun getLocalChamados(): List<ChamadoEntity> = dao.getAll()

    suspend fun criarChamado(titulo: String, descricao: String, usuarioId: Int?) {
        api.criarChamado(titulo, descricao, usuarioId)
        // re-sync
        syncFromServer()
    }

    suspend fun login(email: String, senha: String) = api.login(email, senha)
    suspend fun registrar(nome: String, email: String, senha: String, role: String = "usuario") = api.registrar(nome, email, senha, role)

    suspend fun atualizarStatus(chamadoId: Int, status: String) {
        api.atualizarStatus(chamadoId, status)
        // re-sync
        syncFromServer()
    }
}
