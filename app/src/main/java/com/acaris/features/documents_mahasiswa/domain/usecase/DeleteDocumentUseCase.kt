package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import javax.inject.Inject

class DeleteDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentId: String): Result<Boolean> {
        return repository.deleteDocument(documentId)
    }
}