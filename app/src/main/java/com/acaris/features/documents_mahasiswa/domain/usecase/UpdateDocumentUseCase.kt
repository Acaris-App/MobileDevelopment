package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import java.io.File
import javax.inject.Inject

class UpdateDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentId: String, semester: Int?, file: File?): Result<Document> {

        if (documentId.isBlank()) {
            return Result.failure(Exception("ID Dokumen tidak valid."))
        }

        if (semester != null && semester != 0 && (semester < 1 || semester > 14)) {
            return Result.failure(Exception("Semester tidak valid. Harap masukkan semester 1 - 14."))
        }

        if (file != null) {
            if (!file.exists() || file.length() == 0L) {
                return Result.failure(Exception("File dokumen tidak ditemukan atau kosong."))
            }

            if (file.extension.lowercase() != "pdf") {
                return Result.failure(Exception("Format file baru harus berupa PDF."))
            }

            if (file.length() > 1048576) {
                return Result.failure(Exception("Ukuran file maksimal 1 MB."))
            }
        }

        return repository.updateDocument(documentId, semester, file)
    }
}