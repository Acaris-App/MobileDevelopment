package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(id: String, name: String?, email: String?, identifierNumber: String?): Result<User> {
        return repository.updateUser(id, name, email, identifierNumber)
    }
}