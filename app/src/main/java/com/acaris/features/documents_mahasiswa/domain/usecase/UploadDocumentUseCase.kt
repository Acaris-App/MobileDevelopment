package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import java.io.File
import javax.inject.Inject

class UploadDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentType: String, semester: Int?, file: File): Result<Document> {

        // Validasi Logika Bisnis Murni
        if (documentType !in listOf("krs", "khs", "transkrip")) {
            return Result.failure(Exception("Jenis dokumen tidak valid."))
        }

        if (documentType != "transkrip" && semester == null) {
            return Result.failure(Exception("Semester wajib diisi untuk KRS dan KHS."))
        }

        if (!file.exists() || file.length() == 0L) {
            return Result.failure(Exception("File PDF tidak ditemukan atau kosong."))
        }

        if (file.length() > 1048576) { // 1 MB
            return Result.failure(Exception("Ukuran file maksimal 1 MB."))
        }

        return repository.uploadDocument(documentType, semester, file)
    }
}