package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.BimbinganHistory
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetBimbinganHistoryUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(userId: String): Result<List<BimbinganHistory>> {
        return repository.getBimbinganHistory(userId)
    }
}