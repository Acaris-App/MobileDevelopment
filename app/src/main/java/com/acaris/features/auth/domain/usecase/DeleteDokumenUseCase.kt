package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteDokumenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(documentId: Int): Result<Unit> {
        return repository.deleteDokumen(documentId)
    }
}