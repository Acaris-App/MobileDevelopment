package com.acaris.features.auth.data.remote.datasource

import com.acaris.features.auth.data.remote.model.LoginRequestModel
import com.acaris.features.auth.data.remote.model.LoginResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestModel
    ): Response<LoginResponseModel>
}