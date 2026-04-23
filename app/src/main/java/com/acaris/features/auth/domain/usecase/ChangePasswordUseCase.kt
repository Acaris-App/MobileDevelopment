package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String, confirmNewPassword: String): Result<Unit> {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
            return Result.failure(Exception("Semua kolom password harus diisi."))
        }

        if (newPassword != confirmNewPassword) {
            return Result.failure(Exception("Konfirmasi password baru tidak cocok."))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("Password baru minimal 8 karakter."))
        }

        if (oldPassword == newPassword) {
            return Result.failure(Exception("Password baru tidak boleh sama dengan password lama."))
        }

        return repository.changePassword(oldPassword, newPassword)
    }
}