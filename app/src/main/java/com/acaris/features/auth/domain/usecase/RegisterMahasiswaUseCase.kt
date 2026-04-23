package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import java.io.File
import javax.inject.Inject

class RegisterMahasiswaUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        npm: String, name: String, email: String, password: String,
        angkatan: Int, currentSemester: Int, ipk: Double, kodeKelas: String,
        profilePicture: File? = null
    ): Result<Unit> {

        if (npm.isBlank() || name.isBlank() || email.isBlank() || kodeKelas.isBlank()) {
            return Result.failure(Exception("Semua kolom data diri wajib diisi."))
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        if (!email.matches(emailRegex)) {
            return Result.failure(Exception("Format email tidak valid."))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Password minimal harus 8 karakter."))
        }

        if (currentSemester < 1 || angkatan < 1990) {
            return Result.failure(Exception("Data semester atau angkatan tidak valid."))
        }

        if (ipk < 0.0 || ipk > 4.0) {
            return Result.failure(Exception("IPK tidak valid. Harap masukkan nilai antara 0.00 hingga 4.00."))
        }

        return repository.registerMahasiswa(
            npm, name, email, password, angkatan, currentSemester, ipk, kodeKelas, profilePicture
        )
    }
}