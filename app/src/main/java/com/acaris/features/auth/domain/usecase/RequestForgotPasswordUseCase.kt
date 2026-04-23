package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RequestForgotPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

        if (email.isBlank() || !email.matches(emailRegex)) {
            return Result.failure(Exception("Format email tidak valid."))
        }
        return repository.requestForgotPasswordOtp(email)
    }
}