package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(): Result<List<Document>> {
        return repository.getDocuments()
    }
}