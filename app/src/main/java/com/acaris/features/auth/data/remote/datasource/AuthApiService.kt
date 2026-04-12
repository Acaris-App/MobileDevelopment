package com.acaris.features.auth.data.remote.datasource

import com.acaris.core.network.model.BaseResponse
import com.acaris.features.auth.data.remote.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestModel): Response<BaseResponse<LoginResponseModel>>

    @POST("auth/validate-kode-kelas")
    suspend fun validateKodeKelas(@Body request: ValidateKodeKelasRequest): Response<BaseResponse<Any>>

    @Multipart
    @POST("auth/register/mahasiswa")
    suspend fun registerMahasiswa(
        @Part("npm_nip") npm: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("angkatan") angkatan: RequestBody,
        @Part("current_semester") current_semester: RequestBody,
        @Part("ipk") ipk: RequestBody,
        @Part("kode_kelas") kodeKelas: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ): Response<BaseResponse<Any>>

    @Multipart
    @POST("auth/register/dosen")
    suspend fun registerDosen(
        @Part("npm_nip") nip: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part profile_picture: MultipartBody.Part?
    ): Response<BaseResponse<Any>>

    @POST("auth/verify-register-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<BaseResponse<LoginResponseModel>>

    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<BaseResponse<Any>>

    @POST("auth/forgot-password")
    suspend fun requestForgotPasswordOtp(@Body request: ForgotPasswordRequest): Response<BaseResponse<Any>>

    @POST("auth/verify-reset-otp")
    suspend fun verifyResetPasswordOtp(@Body request: VerifyResetOtpRequest): Response<BaseResponse<Any>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<BaseResponse<Any>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse<Any>>

    @POST("auth/logout")
    suspend fun logout(): Response<BaseResponse<Any>>
}