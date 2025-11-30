package com.example.suporte.di

import com.example.suporte.model.Chamado
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
}
