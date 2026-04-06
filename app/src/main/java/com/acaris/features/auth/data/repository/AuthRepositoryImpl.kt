package com.acaris.features.auth.data.repository

import com.acaris.core.datastore.AuthPreferences
import com.acaris.features.auth.data.mapper.toDomain
import com.acaris.features.auth.data.remote.datasource.AuthApiService
import com.acaris.features.auth.data.remote.model.LoginRequestModel
import com.acaris.features.auth.data.remote.model.ValidateKodeKelasRequest
import com.acaris.features.auth.data.remote.model.VerifyOtpRequest
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

    override suspend fun validateKodeKelas(kodeKelas: String): Result<Unit> {
        return try {
            val response = apiService.validateKodeKelas(ValidateKodeKelasRequest(kodeKelas))
            val body = response.body()

            if (response.isSuccessful && body?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Kode kelas tidak ditemukan."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi Anda."))
        }
    }

    // ==========================================
    // 🌟 IMPLEMENTASI REGISTER (MULTIPART)
    // ==========================================

    override suspend fun registerMahasiswa(
        npm: String, name: String, email: String, password: String,
        angkatan: Int, currentSemester: Int, kodeKelas: String, profilePicture: File?
    ): Result<Unit> {
        return try {
            // 1. Ubah SEMUA teks menjadi RequestBody
            val npmPart = npm.toRequestBody("text/plain".toMediaTypeOrNull())
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val angkatanPart = angkatan.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val semesterPart = currentSemester.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val kodeKelasPart = kodeKelas.toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Siapkan File Gambar (Jika Ada)
            val imagePart = profilePicture?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }

            // 3. Tembak ke API Service
            val response = apiService.registerMahasiswa(
                npm = npmPart,
                name = namePart,
                email = emailPart,
                password = passwordPart,
                angkatan = angkatanPart,
                semester = semesterPart,
                kodeKelas = kodeKelasPart,
                profile_picture = imagePart
            )

            val body = response.body()
            if (response.isSuccessful && body?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Gagal mendaftarkan akun mahasiswa."))
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
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
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
            if (response.isSuccessful && body?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Gagal mendaftarkan akun dosen."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server."))
        }
    }

    // ==========================================

    override suspend fun uploadDokumen(
        documentType: String,
        semester: Int?,
        file: File
    ): Result<Unit> {
        return try {
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val typePart = documentType.toRequestBody("text/plain".toMediaTypeOrNull())
            val semesterPart = semester?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.uploadDokumen(typePart, semesterPart, filePart)
            val body = response.body()

            if (response.isSuccessful && body?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.message ?: "Gagal mengunggah dokumen."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server saat mengunggah file."))
        }
    }

    override suspend fun verifyOtp(email: String, otpCode: String): Result<User> {
        return try {
            val request = VerifyOtpRequest(email, otpCode)
            val response = apiService.verifyOtp(request)
            val body = response.body()

            if (response.isSuccessful && body?.status == true && body.data != null) {
                val token = body.data.token
                val userDomain = body.data.user.toDomain(token)

                authPreferences.saveAuthSession(
                    token = token,
                    role = userDomain.role.lowercase()
                )

                Result.success(userDomain)
            } else {
                Result.failure(Exception(body?.message ?: "Kode OTP salah atau kedaluwarsa."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Gagal terhubung ke server. Periksa koneksi Anda."))
        }
    }
}