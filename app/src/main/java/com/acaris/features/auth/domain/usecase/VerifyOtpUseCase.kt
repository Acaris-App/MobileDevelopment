package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String): Result<User> {
        if (email.isBlank() || otpCode.isBlank()) {
            return Result.failure(Exception("Email dan kode OTP tidak boleh kosong."))
        }

        if (otpCode.length < 6) {
            return Result.failure(Exception("Format kode OTP tidak valid."))
        }

        return repository.verifyOtp(email, otpCode)
    }
}