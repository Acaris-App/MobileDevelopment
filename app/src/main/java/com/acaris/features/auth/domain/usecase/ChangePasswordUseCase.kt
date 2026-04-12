package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String, confirmNewPassword: String): Result<Unit> {
        // 1. Validasi Input Kosong
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
            return Result.failure(Exception("Semua kolom password harus diisi."))
        }

        // 2. Validasi Konfirmasi Password
        if (newPassword != confirmNewPassword) {
            return Result.failure(Exception("Konfirmasi password baru tidak cocok."))
        }

        // 3. Validasi Panjang Password
        if (newPassword.length < 8) {
            return Result.failure(Exception("Password baru minimal 8 karakter."))
        }

        // 4. Validasi Keamanan (Password baru jangan sama dengan yang lama)
        if (oldPassword == newPassword) {
            return Result.failure(Exception("Password baru tidak boleh sama dengan password lama."))
        }

        // Jika semua validasi lokal lolos, lempar ke Repository untuk hit API
        return repository.changePassword(oldPassword, newPassword)
    }
}