package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class DeleteMahasiswaDocumentUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(documentId: String): Result<Unit> {
        return repository.deleteMahasiswaDocument(documentId)
    }
}