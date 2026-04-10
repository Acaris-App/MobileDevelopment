package com.acaris.features.auth.data.repository

import com.acaris.core.network.datastore.AuthPreferences
import com.acaris.features.auth.data.mapper.toDomain
import com.acaris.features.auth.data.remote.datasource.AuthApiService
import com.acaris.features.auth.data.remote.model.ForgotPasswordRequest
import com.acaris.features.auth.data.remote.model.LoginRequestModel
import com.acaris.features.auth.data.remote.model.ResendOtpRequest
import com.acaris.features.auth.data.remote.model.ResetPasswordRequest
import com.acaris.features.auth.data.remote.model.ValidateKodeKelasRequest
import com.acaris.features.auth.data.remote.model.VerifyOtpRequest
import com.acaris.features.auth.data.remote.model.VerifyResetOtpRequest
import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.domain.repository.AuthRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
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

                if (body != null && body.status == "success" && body.data != null) {
                    val token = body.data.token
                    val userDomain = body.data.user.toDomain(token, body.data.role)

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

    override suspend fun validateKodeKelas(kodeKelas: String): Result<Unit> {
        return try {
            val response = apiService.validateKodeKelas(ValidateKodeKelasRequest(kodeKelas))
            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Kode kelas tidak ditemukan."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi Anda."))
        }
    }

    override suspend fun registerMahasiswa(
        npm: String, name: String, email: String, password: String,
        angkatan: Int, currentSemester: Int, ipk: Double, kodeKelas: String, profilePicture: File? // 🌟 TAMBAH IPK
    ): Result<Unit> {
        return try {
            val npmPart = npm.toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val angkatanPart = angkatan.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val currentSemesterPart = currentSemester.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val ipkPart = ipk.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val kodeKelasPart = kodeKelas.toRequestBody("text/plain".toMediaTypeOrNull())
            val imagePart = profilePicture?.let { file ->
                val mimeType = when (file.extension.lowercase()) {
                    "png" -> "image/png"
                    "webp" -> "image/webp"
                    else -> "image/jpeg"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            val response = apiService.registerMahasiswa(
                npm = npmPart,
                name = namePart,
                email = emailPart,
                password = passwordPart,
                angkatan = angkatanPart,
                current_semester = currentSemesterPart,
                ipk = ipkPart,
                kodeKelas = kodeKelasPart,
                profile_picture = imagePart
            )

            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMessage = try {
                    org.json.JSONObject(errorString ?: "").getString("message")
                } catch (e: Exception) {
                    body?.message ?: "Gagal mendaftarkan akun mahasiswa."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server."))
        }
    }

    override suspend fun registerDosen(
        nip: String, name: String, email: String, password: String, profilePicture: File?
    ): Result<Unit> {
        return try {
            val nipPart = nip.toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = profilePicture?.let { file ->
                val mimeType = when (file.extension.lowercase()) {
                    "png" -> "image/png"
                    "webp" -> "image/webp"
                    else -> "image/jpeg"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            val response = apiService.registerDosen(
                nip = nipPart,
                name = namePart,
                email = emailPart,
                password = passwordPart,
                profile_picture = imagePart
            )

            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMessage = try {
                    org.json.JSONObject(errorString ?: "").getString("message")
                } catch (e: Exception) {
                    body?.message ?: "Gagal mendaftarkan akun dosen."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server."))
        }
    }

    override suspend fun verifyOtp(email: String, otpCode: String): Result<User> {
        return try {
            val request = VerifyOtpRequest(email, otpCode)
            val response = apiService.verifyOtp(request)
            val body = response.body()

            if (response.isSuccessful && body?.status == "success" && body.data != null) {
                val token = body.data.token
                val userDomain = body.data.user.toDomain(token, body.data.role)

                authPreferences.saveAuthSession(
                    token = token,
                    role = userDomain.role.lowercase()
                )

                Result.success(userDomain)
            } else {
                Result.failure(Exception(body?.message ?: "Kode OTP salah atau kedaluwarsa."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error Parsing: ${e.message}"))
        }
    }

    override suspend fun resendOtp(email: String): Result<Unit> {
        return try {
            val response = apiService.resendOtp(ResendOtpRequest(email))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Gagal mengirim ulang OTP. Kode: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestForgotPasswordOtp(email: String): Result<Unit> {
        return try {
            val response = apiService.requestForgotPasswordOtp(ForgotPasswordRequest(email))
            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMessage = try {
                    org.json.JSONObject(errorString ?: "").getString("message")
                } catch (e: Exception) {
                    body?.message ?: "Gagal mengirim permintaan OTP reset password."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun verifyResetPasswordOtp(email: String, otpCode: String): Result<Unit> {
        return try {
            val response = apiService.verifyResetPasswordOtp(VerifyResetOtpRequest(email, otpCode))
            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMessage = try {
                    org.json.JSONObject(errorString ?: "").getString("message")
                } catch (e: Exception) {
                    body?.message ?: "Kode OTP salah atau sudah kedaluwarsa."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun resetPassword(email: String, otpCode: String, newPassword: String): Result<Unit> {
        return try {
            val response = apiService.resetPassword(ResetPasswordRequest(email, otpCode, newPassword))
            val body = response.body()

            if (response.isSuccessful && body?.status == "success") {
                Result.success(Unit)
            } else {
                val errorString = response.errorBody()?.string()
                val errorMessage = try {
                    org.json.JSONObject(errorString ?: "").getString("message")
                } catch (e: Exception) {
                    body?.message ?: "Gagal mereset password. Silakan coba lagi."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi internet Anda."))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()

            authPreferences.clearAuthSession()

            Result.success(Unit)
        } catch (e: Exception) {
            authPreferences.clearAuthSession()
            Result.success(Unit)
        }
    }
}