package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.MahasiswaDocument
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import java.io.File
import javax.inject.Inject

class UpdateMahasiswaDocumentUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(documentId: String, semester: Int?, file: File?): Result<MahasiswaDocument> {

        // Validasi Semester jika admin memutuskan untuk mengubahnya (0 untuk transkrip, 1-14 untuk KRS/KHS)
        if (semester != null && (semester < 0 || semester > 14)) {
            return Result.failure(Exception("Semester tidak valid. Harap masukkan semester 0 - 14."))
        }

        // Validasi File jika admin mengunggah file baru sebagai pengganti
        if (file != null) {
            if (!file.exists() || file.length() == 0L) {
                return Result.failure(Exception("File dokumen tidak ditemukan atau kosong."))
            }

            if (file.extension.lowercase() != "pdf") {
                return Result.failure(Exception("Format file harus berupa PDF."))
            }

            if (file.length() > 1048576) { // 1 MB
                return Result.failure(Exception("Ukuran file maksimal 1 MB."))
            }
        }

        return repository.updateMahasiswaDocument(documentId, semester, file)
    }
}