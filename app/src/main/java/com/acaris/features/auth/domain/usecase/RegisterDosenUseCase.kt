package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import java.io.File
import javax.inject.Inject

class RegisterDosenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        nip: String, name: String, email: String, password: String,
        profilePicture: File? = null
    ): Result<Unit> {

        if (nip.isBlank() || name.isBlank() || email.isBlank()) {
            return Result.failure(Exception("Semua kolom data diri wajib diisi."))
        }

        if (!email.contains("@")) {
            return Result.failure(Exception("Format email tidak valid."))
        }

        if (password.length < 8) {
            return Result.failure(Exception("Password minimal harus 8 karakter."))
        }

        return repository.registerDosen(nip, name, email, password, profilePicture)
    }
}