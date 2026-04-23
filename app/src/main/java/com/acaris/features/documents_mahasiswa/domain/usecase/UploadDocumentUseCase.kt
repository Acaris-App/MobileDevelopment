package com.acaris.features.documents_mahasiswa.domain.usecase

import com.acaris.features.documents_mahasiswa.domain.model.Document
import com.acaris.features.documents_mahasiswa.domain.repository.DocumentRepository
import java.io.File
import javax.inject.Inject

class UploadDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(documentType: String, semester: Int?, file: File): Result<Document> {

        if (documentType !in listOf("krs", "khs", "transkrip")) {
            return Result.failure(Exception("Jenis dokumen tidak valid."))
        }

        if (documentType != "transkrip") {
            if (semester == null || semester < 1 || semester > 14) {
                return Result.failure(Exception("Semester tidak valid. Harap masukkan semester 1 - 14 untuk KRS dan KHS."))
            }
        } else {
            if (semester != null && semester != 0) {
                return Result.failure(Exception("Semester transkrip harus bernilai 0."))
            }
        }

        if (!file.exists() || file.length() == 0L) {
            return Result.failure(Exception("File dokumen tidak ditemukan atau kosong."))
        }

        if (file.extension.lowercase() != "pdf") {
            return Result.failure(Exception("Format file harus berupa PDF."))
        }

        if (file.length() > 1048576) { // 1 MB
            return Result.failure(Exception("Ukuran file maksimal 1 MB."))
        }

        return repository.uploadDocument(documentType, semester, file)
    }
}