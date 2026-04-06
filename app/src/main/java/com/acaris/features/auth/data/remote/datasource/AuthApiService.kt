package com.acaris.features.auth.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.auth.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("api/auth/validate-kode-kelas")
    suspend fun validateKodeKelas(@Body request: ValidateKodeKelasRequest): Response<BaseResponse<Any>>

    @Multipart
    @POST("api/auth/register/mahasiswa")
    suspend fun registerMahasiswa(
        @Part("npm") npm: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("angkatan") angkatan: RequestBody,
        @Part("semester") semester: RequestBody,
        @Part("kode_kelas") kodeKelas: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ): Response<BaseResponse<Any>>

    @Multipart
    @POST("api/auth/register/dosen")
    suspend fun registerDosen(
        @Part("nip") nip: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ): Response<BaseResponse<Any>>

    @Multipart
    @POST("api/auth/register/upload-dokumen")
    suspend fun uploadDokumen(
        @Part("document_type") documentType: RequestBody,
        @Part("semester") semester: RequestBody?,
        @Part file: MultipartBody.Part
    ): Response<BaseResponse<Any>>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<BaseResponse<LoginResponseModel>>
}