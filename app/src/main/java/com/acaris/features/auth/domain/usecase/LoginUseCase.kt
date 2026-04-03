package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.model.User
import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {

        if (email.isBlank()) {
            return Result.failure(Exception("Email tidak boleh kosong"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Password tidak boleh kosong"))
        }

        return repository.login(email, password)
    }
}