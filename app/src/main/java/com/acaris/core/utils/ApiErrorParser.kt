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
                // Bongkar brankas errorBody()
                val errorJsonString = this.response()?.errorBody()?.string()
                val parsedError = Gson().fromJson(errorJsonString, ErrorResponse::class.java)

                // Kembalikan pesan asli dari Backend
                parsedError.message ?: "Terjadi kesalahan pada server."
            } catch (e: Exception) {
                "Terjadi kesalahan. (HTTP ${this.code()})"
            }
        }
        // Jika errornya karena mati lampu / tidak ada internet
        else -> this.localizedMessage ?: "Koneksi bermasalah atau error tidak diketahui."
    }
}