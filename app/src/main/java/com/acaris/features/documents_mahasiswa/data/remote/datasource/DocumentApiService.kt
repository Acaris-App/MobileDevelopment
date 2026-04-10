package com.acaris.features.documents_mahasiswa.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.documents_mahasiswa.data.remote.model.DocumentListDataResponse
import com.acaris.features.documents_mahasiswa.data.remote.model.DocumentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DocumentApiService {

    @GET("document/list")
    suspend fun getDocuments(): BaseResponse<DocumentListDataResponse>

    @Multipart
    @POST("document/upload")
    suspend fun uploadDocument(
        @Part("document_type") type: RequestBody,
        @Part("semester") semester: RequestBody?,
        @Part file: MultipartBody.Part
    ): BaseResponse<DocumentResponse>

    @Multipart
    @PUT("document/update/{document_id}")
    suspend fun updateDocument(
        @Path("document_id") documentId: String,
        @Part("semester") semester: RequestBody?,
        @Part file: MultipartBody.Part?
    ): BaseResponse<DocumentResponse>

    @DELETE("document/delete/{document_id}")
    suspend fun deleteDocument(
        @Path("document_id") documentId: String
    ): BaseResponse<Any>
}