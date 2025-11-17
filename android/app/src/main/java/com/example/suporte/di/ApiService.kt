package com.example.suporte.di

import com.example.suporte.model.Chamado
import retrofit2.http.*

interface ApiService {
    @GET("api.php?acao=listar")
    suspend fun listarChamados(): List<Chamado>

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
        @Field("senha") senha: String
    ): Map<String, Any>
}
