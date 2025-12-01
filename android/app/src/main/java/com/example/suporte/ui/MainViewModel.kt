package com.example.suporte.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.suporte.data.Repository
import com.example.suporte.db.ChamadoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context

class MainViewModel(private val context: Context) : ViewModel() {
    private val repo = Repository(context)
    private val _chamados = MutableStateFlow<List<ChamadoEntity>>(emptyList())
    val chamados: StateFlow<List<ChamadoEntity>> = _chamados

    fun carregar() {
        viewModelScope.launch {
            try {
                repo.syncFromServer()
                val local = repo.getLocalChamados()
                _chamados.value = local
            } catch (e: Exception) {
                // em caso de erro, tenta carregar local
                val local = repo.getLocalChamados()
                _chamados.value = local
            }
        }
    }

    fun criar(titulo: String, descricao: String, usuarioId: Int?) {
        viewModelScope.launch {
            try {
                repo.criarChamado(titulo, descricao, usuarioId)
                val local = repo.getLocalChamados()
                _chamados.value = local
            } catch (e: Exception) {
                // tratar erro
            }
        }
    }

    fun login(email: String, senha: String, onResult: (Map<String, Any>?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.login(email, senha)
                onResult(res)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun registrar(nome: String, email: String, senha: String, role: String = "usuario", onResult: (Map<String, Any>?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.registrar(nome, email, senha, role)
                onResult(res)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun atualizarStatus(chamadoId: Int, status: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repo.atualizarStatus(chamadoId, status)
                val local = repo.getLocalChamados()
                _chamados.value = local
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun editarChamado(chamadoId: Int, titulo: String, descricao: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repo.editarChamado(chamadoId, titulo, descricao)
                val local = repo.getLocalChamados()
                _chamados.value = local
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun excluirChamado(chamadoId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repo.excluirChamado(chamadoId)
                val local = repo.getLocalChamados()
                _chamados.value = local
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun salvarFcmToken(usuarioId: Int, token: String) {
        viewModelScope.launch {
            try {
                repo.salvarFcmToken(usuarioId, token)
            } catch (e: Exception) {
                // Log erro mas não interrompe o fluxo
                android.util.Log.e("FCM", "Erro ao salvar token: ${e.message}")
            }
        }
    }

    // === Métodos para Gerenciamento de Usuários ===
    suspend fun listarUsuarios() = repo.listarUsuarios()

    suspend fun criarUsuario(nome: String, email: String, senha: String, role: String) =
        repo.criarUsuario(nome, email, senha, role)

    suspend fun editarUsuario(usuarioId: Int, nome: String, email: String, senha: String, role: String) =
        repo.editarUsuario(usuarioId, nome, email, senha, role)

    suspend fun excluirUsuario(usuarioId: Int) = repo.excluirUsuario(usuarioId)

    // === Métodos para Gerenciamento de Status ===
    suspend fun listarStatus() = repo.listarStatus()

    suspend fun listarStatusAtivos() = repo.listarStatusAtivos()

    suspend fun criarStatus(nome: String, ativo: Boolean = true) =
        repo.criarStatus(nome, ativo)

    suspend fun atualizarStatus(id: Int, nome: String, ativo: Boolean) =
        repo.atualizarStatus(id, nome, ativo)

    suspend fun excluirStatus(id: Int) = repo.excluirStatus(id)
}
