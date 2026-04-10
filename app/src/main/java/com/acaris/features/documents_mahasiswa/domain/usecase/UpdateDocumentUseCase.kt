package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import java.io.File
import javax.inject.Inject

class UpdateDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentId: String, semester: Int?, file: File?): Result<Document> {
        if (file != null) {
            if (!file.exists() || file.length() == 0L) return Result.failure(Exception("File PDF tidak ditemukan atau kosong."))
            if (file.length() > 1048576) return Result.failure(Exception("Ukuran file maksimal 1 MB."))
        }

        return repository.updateDocument(documentId, semester, file)
    }
}