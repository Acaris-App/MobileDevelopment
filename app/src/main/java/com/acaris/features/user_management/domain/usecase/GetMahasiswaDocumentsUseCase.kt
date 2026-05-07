package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.MahasiswaDocument
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetMahasiswaDocumentsUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(userId: String): Result<List<MahasiswaDocument>> {
        return repository.getMahasiswaDocuments(userId)
    }
}