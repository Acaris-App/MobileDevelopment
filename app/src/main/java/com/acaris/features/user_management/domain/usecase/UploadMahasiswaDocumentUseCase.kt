package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.MahasiswaDocument
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import java.io.File
import javax.inject.Inject

class UploadMahasiswaDocumentUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(userId: String, documentType: String, semester: Int?, file: File): Result<MahasiswaDocument> {

        // Aturan Bisnis 1: Validasi Tipe Dokumen
        val validTypes = listOf("krs", "khs", "transkrip")
        if (documentType.lowercase() !in validTypes) {
            return Result.failure(Exception("Jenis dokumen tidak valid."))
        }

        // Aturan Bisnis 2: Validasi Semester berdasarkan Tipe
        if (documentType.lowercase() != "transkrip") {
            if (semester == null || semester < 1 || semester > 14) {
                return Result.failure(Exception("Semester tidak valid. Harap masukkan semester 1 - 14 untuk KRS dan KHS."))
            }
        } else {
            if (semester != null && semester != 0) {
                return Result.failure(Exception("Semester transkrip harus bernilai 0."))
            }
        }

        // Aturan Bisnis 3: Validasi File (Keberadaan, Format, Ukuran)
        if (!file.exists() || file.length() == 0L) {
            return Result.failure(Exception("File dokumen tidak ditemukan atau kosong."))
        }

        if (file.extension.lowercase() != "pdf") {
            return Result.failure(Exception("Format file harus berupa PDF."))
        }

        if (file.length() > 1048576) { // 1 MB
            return Result.failure(Exception("Ukuran file maksimal 1 MB."))
        }

        return repository.uploadMahasiswaDocument(userId, documentType.lowercase(), semester, file)
    }
}