package com.acaris.features.auth.domain.repository

import com.acaris.features.auth.domain.model.User
import java.io.File

interface AuthRepository {

    suspend fun login(email: String, password: String): Result<User>

    suspend fun validateKodeKelas(kodeKelas: String): Result<Unit>

    suspend fun uploadDokumen(
        documentType: String,
        semester: Int?,
        file: File
    ): Result<Unit>

    suspend fun verifyOtp(email: String, otpCode: String): Result<User>

    suspend fun registerMahasiswa(
        npm: String,
        name: String,
        email: String,
        password: String,
        angkatan: Int,
        currentSemester: Int,
        kodeKelas: String,
        profilePicture: File? = null
    ): Result<Unit>

    suspend fun registerDosen(
        nip: String,
        name: String,
        email: String,
        password: String,
        profilePicture: File? = null
    ): Result<Unit>
}