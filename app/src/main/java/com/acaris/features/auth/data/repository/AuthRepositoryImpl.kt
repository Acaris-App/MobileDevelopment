package com.acaris.features.auth.data.repository

import com.acaris.core.datastore.AuthPreferences
import com.acaris.features.auth.data.mapper.toDomain
import com.acaris.features.auth.data.remote.datasource.AuthApiService
import com.acaris.features.auth.data.remote.model.LoginRequestModel
import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val authPreferences: AuthPreferences
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequestModel(email = email, password = password)
            val response = apiService.login(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status && body.data != null) {

                    val token = body.data.token
                    val userDomain = body.data.user.toDomain(token)

                    authPreferences.saveAuthSession(
                        token = token,
                        role = userDomain.role.lowercase()
                    )

                    Result.success(userDomain)
                } else {
                    Result.failure(Exception(body?.message ?: "Login gagal."))
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Email tidak ditemukan. Silakan daftar terlebih dahulu."
                    401 -> "Password salah. Periksa kembali kombinasi sandi Anda."
                    403 -> "Akses ditolak. Akun Anda mungkin diblokir."
                    500 -> "Server sedang bermasalah. Coba lagi nanti."
                    else -> "Terjadi kesalahan sistem (Kode: ${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }
}