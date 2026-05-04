package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class ChangeUserStatusUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(id: String, isActive: Boolean): Result<Unit> {
        return repository.changeUserStatus(id, isActive)
    }
}