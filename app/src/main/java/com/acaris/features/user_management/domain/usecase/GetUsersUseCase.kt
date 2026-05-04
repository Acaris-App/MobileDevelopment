package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(
        role: String,
        search: String? = null,
        sortBy: String? = null
    ): Result<List<User>> {
        return repository.getUsers(role, search, sortBy)
    }
}