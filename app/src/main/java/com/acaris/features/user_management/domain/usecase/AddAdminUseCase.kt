package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import java.io.File
import javax.inject.Inject

class AddAdminUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        nip: String,
        password: String,
        profilePicture: File?
    ): Result<User> {
        if (name.isBlank() || email.isBlank() || nip.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Nama, Email, NIP, dan Password wajib diisi!"))
        }
        if (!email.contains("@")) {
            return Result.failure(Exception("Format email tidak valid!"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password minimal 6 karakter!"))
        }

        return repository.addAdmin(name, email, nip, password, profilePicture)
    }
}