package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import javax.inject.Inject

class DeleteDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentId: String): Result<Boolean> {
        if (documentId.isBlank()) {
            return Result.failure(Exception("ID Dokumen tidak valid atau kosong."))
        }

        return repository.deleteDocument(documentId)
    }
}