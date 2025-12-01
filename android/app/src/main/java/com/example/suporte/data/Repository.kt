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
    private val prefs = context.getSharedPreferences("suporte_prefs", android.content.Context.MODE_PRIVATE)

    // fetch from server, update local DB
    suspend fun syncFromServer() {
        val userId = prefs.getInt("user_id", -1)
        val userRole = prefs.getString("user_role", "usuario") ?: "usuario"

        val list = if (userRole == "tecnico") {
            // Técnicos veem todos os chamados
            api.listarChamados(userRole = "tecnico")
        } else {
            // Usuários veem apenas os seus
            if (userId != -1) {
                api.listarChamados(usuarioId = userId, userRole = "usuario")
            } else {
                emptyList()
            }
        }

        val entities = list.map { ChamadoEntity(it.id, it.titulo, it.descricao, it.status, it.data_abertura, it.usuario_id, it.email_usuario) }
        dao.clear() // Limpar antes de inserir novos dados filtrados
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

    suspend fun editarChamado(chamadoId: Int, titulo: String, descricao: String) {
        val userId = prefs.getInt("user_id", -1)
        val userRole = prefs.getString("user_role", "usuario") ?: "usuario"
        api.editarChamado(chamadoId, titulo, descricao, userId, userRole)
        // re-sync
        syncFromServer()
    }

    suspend fun excluirChamado(chamadoId: Int) {
        val userId = prefs.getInt("user_id", -1)
        val userRole = prefs.getString("user_role", "usuario") ?: "usuario"
        api.excluirChamado(chamadoId, userId, userRole)
        // re-sync
        syncFromServer()
    }

    suspend fun salvarFcmToken(usuarioId: Int, token: String) = api.salvarFcmToken(usuarioId, token)

    // === CRUD de Usuários ===
    suspend fun listarUsuarios() = api.listarUsuarios()

    suspend fun criarUsuario(nome: String, email: String, senha: String, role: String) =
        api.criarUsuario(nome, email, senha, role)

    suspend fun editarUsuario(usuarioId: Int, nome: String, email: String, senha: String, role: String) =
        api.editarUsuario(usuarioId, nome, email, senha, role)

    suspend fun excluirUsuario(usuarioId: Int) = api.excluirUsuario(usuarioId)

    // === CRUD de Status ===
    suspend fun listarStatus() = api.listarStatus()

    suspend fun listarStatusAtivos() = api.listarStatusAtivos()

    suspend fun criarStatus(nome: String, ativo: Boolean = true) =
        api.criarStatus(nome, ativo)

    suspend fun atualizarStatus(id: Int, nome: String, ativo: Boolean) =
        api.atualizarStatus(id, nome, ativo)

    suspend fun excluirStatus(id: Int) = api.excluirStatus(id)
}
