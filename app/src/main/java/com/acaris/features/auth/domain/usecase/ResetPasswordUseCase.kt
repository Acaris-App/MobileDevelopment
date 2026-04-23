package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String, newPassword: String, confirmPassword: String): Result<Unit> {
        if (newPassword.isBlank() || confirmPassword.isBlank()) {
            return Result.failure(Exception("Password tidak boleh kosong."))
        }

        if (newPassword != confirmPassword) {
            return Result.failure(Exception("Konfirmasi password tidak cocok."))
        }

        if (newPassword.length < 8) {
            return Result.failure(Exception("Password harus terdiri dari minimal 8 karakter."))
        }

        return repository.resetPassword(email, otpCode, newPassword)
    }
}