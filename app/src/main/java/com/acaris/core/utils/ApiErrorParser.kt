package com.acaris.core.network

import com.google.gson.Gson
import retrofit2.HttpException

data class ErrorResponse(
    val status: String?,
    val message: String?
)

fun Throwable.parseApiError(): String {
    return when (this) {
        is HttpException -> {
            try {
                val errorJsonString = this.response()?.errorBody()?.string()
                val parsedError = Gson().fromJson(errorJsonString, ErrorResponse::class.java)

                parsedError.message ?: "Terjadi kesalahan pada server."
            } catch (e: Exception) {
                "Terjadi kesalahan. (HTTP ${this.code()})"
            }
        }
        else -> this.localizedMessage ?: "Koneksi bermasalah atau error tidak diketahui."
    }
}