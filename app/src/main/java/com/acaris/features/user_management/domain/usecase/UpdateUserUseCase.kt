package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import java.io.File
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(
        id: String, name: String, email: String, identifier: String,
        angkatan: Int?, currentSemester: Int?, dosenPa: String?, kodeKelas: String?, ipk: Double?,
        profilePicture: File?
    ): Result<User> {
        if (name.isBlank() || email.isBlank() || identifier.isBlank()) {
            return Result.failure(Exception("Nama, Email, dan NIP/NPM tidak boleh kosong!"))
        }
        if (!email.contains("@")) {
            return Result.failure(Exception("Format email tidak valid!"))
        }

        return repository.updateUser(
            id, name, email, identifier,
            angkatan, currentSemester, dosenPa, kodeKelas, ipk,
            profilePicture
        )
    }
}