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

    fun registrar(nome: String, email: String, senha: String, onResult: (Map<String, Any>?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.registrar(nome, email, senha)
                onResult(res)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}
