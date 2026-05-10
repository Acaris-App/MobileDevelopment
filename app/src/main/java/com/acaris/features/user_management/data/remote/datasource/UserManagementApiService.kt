package com.acaris.features.user_management.data.remote.datasource

import com.acaris.features.user_management.data.remote.model.UserResponse
import com.acaris.features.user_management.data.remote.model.MahasiswaDocumentResponse
import com.acaris.features.user_management.data.remote.model.BimbinganHistoryResponse
import com.acaris.core.network.model.BaseResponse
import com.acaris.features.user_management.data.remote.model.ClassInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface UserManagementApiService {

    @GET("admin/users")
    suspend fun getUsers(
        @Query("role") role: String,
        @Query("search") search: String?,
        @Query("sort_by") sortBy: String?,
        @Query("page") page: Int
    ): BaseResponse<List<UserResponse>>

    @Multipart
    @POST("admin/users/admin")
    suspend fun addAdmin(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("identifier") npmNip: RequestBody,
        @Part("password") password: RequestBody,
        @Part profilePicture: MultipartBody.Part?
    ): BaseResponse<UserResponse>

    @Multipart
    @PUT("admin/users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("identifier") identifier: RequestBody?,
        @Part("angkatan") angkatan: RequestBody?,
        @Part("current_semester") semester: RequestBody?,
        @Part("dosen_pa") dosenPa: RequestBody?,
        @Part("kode_kelas") kodeKelas: RequestBody?,
        @Part("ipk") ipk: RequestBody?,
        @Part profilePicture: MultipartBody.Part?
    ): BaseResponse<UserResponse>

    @FormUrlEncoded
    @PATCH("admin/users/{id}/status")
    suspend fun changeUserStatus(
        @Path("id") id: String,
        @Field("is_active") isActive: Boolean
    ): BaseResponse<Any>

    @DELETE("admin/users/{id}")
    suspend fun deleteUserPermanently(
        @Path("id") id: String
    ): BaseResponse<Any>

    @GET("admin/classes")
    suspend fun getAllClasses(): BaseResponse<List<ClassInfoResponse>>

    // ==========================================
    // 🌟 ENDPOINT DETAIL MAHASISWA (READ)
    // ==========================================
    @GET("admin/users/{id}/documents")
    suspend fun getMahasiswaDocuments(
        @Path("id") userId: String
    ): BaseResponse<List<MahasiswaDocumentResponse>>

    @GET("admin/users/{id}/bimbingan")
    suspend fun getBimbinganHistory(
        @Path("id") userId: String
    ): BaseResponse<List<BimbinganHistoryResponse>>

    // ==========================================
    // 🌟 ENDPOINT CRUD DOKUMEN MAHASISWA (ADMIN)
    // ==========================================
    @Multipart
    @POST("admin/users/{id}/documents")
    suspend fun uploadMahasiswaDocument(
        @Path("id") userId: String,
        @Part("document_type") type: RequestBody,
        @Part("semester") semester: RequestBody?,
        @Part file: MultipartBody.Part
    ): BaseResponse<MahasiswaDocumentResponse>

    @Multipart
    @PUT("admin/documents/{document_id}")
    suspend fun updateMahasiswaDocument(
        @Path("document_id") documentId: String,
        @Part("semester") semester: RequestBody?,
        @Part file: MultipartBody.Part?
    ): BaseResponse<MahasiswaDocumentResponse>

    @DELETE("admin/documents/{document_id}")
    suspend fun deleteMahasiswaDocument(
        @Path("document_id") documentId: String
    ): BaseResponse<Any>
}