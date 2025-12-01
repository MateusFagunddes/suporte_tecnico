package com.example.suporte.di

import com.example.suporte.model.Chamado
import com.example.suporte.model.Usuario
import retrofit2.http.*

interface ApiService {
    @GET("api.php")
    suspend fun listarChamados(
        @Query("acao") acao: String = "listar",
        @Query("usuario_id") usuarioId: Int? = null,
        @Query("user_role") userRole: String = "usuario"
    ): List<Chamado>

    @FormUrlEncoded
    @POST("api.php?acao=criar")
    suspend fun criarChamado(
        @Field("titulo") titulo: String,
        @Field("descricao") descricao: String,
        @Field("usuario_id") usuarioId: Int?
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=login")
    suspend fun login(
        @Field("email") email: String,
        @Field("senha") senha: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=registrar")
    suspend fun registrar(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
        @Field("role") role: String = "usuario"
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=atualizar_status")
    suspend fun atualizarStatus(
        @Field("chamado_id") chamadoId: Int,
        @Field("status") status: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=editar_chamado")
    suspend fun editarChamado(
        @Field("chamado_id") chamadoId: Int,
        @Field("titulo") titulo: String,
        @Field("descricao") descricao: String,
        @Field("usuario_id") usuarioId: Int,
        @Field("user_role") userRole: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=excluir_chamado")
    suspend fun excluirChamado(
        @Field("chamado_id") chamadoId: Int,
        @Field("usuario_id") usuarioId: Int,
        @Field("user_role") userRole: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=salvar_fcm_token")
    suspend fun salvarFcmToken(
        @Field("usuario_id") usuarioId: Int,
        @Field("fcm_token") fcmToken: String
    ): Map<String, Any>

    // === CRUD de Usuários ===
    @GET("api.php")
    suspend fun listarUsuarios(
        @Query("acao") acao: String = "listar_usuarios"
    ): List<Usuario>

    @FormUrlEncoded
    @POST("api.php?acao=criar_usuario")
    suspend fun criarUsuario(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
        @Field("role") role: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=editar_usuario")
    suspend fun editarUsuario(
        @Field("usuario_id") usuarioId: Int,
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
        @Field("role") role: String
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=excluir_usuario")
    suspend fun excluirUsuario(
        @Field("usuario_id") usuarioId: Int
    ): Map<String, Any>

    // ============ ENDPOINTS PARA GERENCIAMENTO DE STATUS ============

    @GET("api.php?acao=listar_status")
    suspend fun listarStatus(): List<com.example.suporte.model.Status>

    @GET("api.php?acao=listar_status_ativos")
    suspend fun listarStatusAtivos(): List<String>

    @FormUrlEncoded
    @POST("api.php?acao=criar_status")
    suspend fun criarStatus(
        @Field("nome") nome: String,
        @Field("ativo") ativo: Boolean = true
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=atualizar_status")
    suspend fun atualizarStatus(
        @Field("id") id: Int,
        @Field("nome") nome: String,
        @Field("ativo") ativo: Boolean
    ): Map<String, Any>

    @FormUrlEncoded
    @POST("api.php?acao=excluir_status")
    suspend fun excluirStatus(
        @Field("id") id: Int
    ): Map<String, Any>
}
