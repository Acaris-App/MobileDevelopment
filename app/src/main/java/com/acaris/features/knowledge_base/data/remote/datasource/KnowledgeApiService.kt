package com.acaris.features.knowledge_base.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.knowledge_base.data.remote.model.KnowledgeDocumentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface KnowledgeApiService {

    @GET("admin/knowledge-base")
    suspend fun getKnowledgeDocuments(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): BaseResponse<List<KnowledgeDocumentResponse>>

    @Multipart
    @POST("admin/knowledge-base")
    suspend fun uploadKnowledgeDocument(
        @Part("title") title: RequestBody,
        @Part("category") category: RequestBody,
        @Part file: MultipartBody.Part
    ): BaseResponse<KnowledgeDocumentResponse>

    @Multipart
    @PUT("admin/knowledge-base/{id}")
    suspend fun updateKnowledgeDocument(
        @Path("id") id: String,
        @Part("title") title: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part file: MultipartBody.Part?
    ): BaseResponse<KnowledgeDocumentResponse>

    @DELETE("admin/knowledge-base/{id}")
    suspend fun deleteKnowledgeDocument(
        @Path("id") id: String
    ): BaseResponse<Any>
}