package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyResetPasswordOtpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, otpCode: String): Result<Unit> {
        if (otpCode.length < 4) {
            return Result.failure(Exception("Kode OTP tidak valid."))
        }
        return repository.verifyResetPasswordOtp(email, otpCode)
    }
}