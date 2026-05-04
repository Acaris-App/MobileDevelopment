package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class AddAdminUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<User> {
        // 🛡️ Business Rule: Validasi Input
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Nama, Email, dan Password tidak boleh kosong!"))
        }
        if (!email.contains("@")) {
            return Result.failure(Exception("Format email tidak valid!"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password minimal 6 karakter!"))
        }

        return repository.addAdmin(name, email, password)
    }
}