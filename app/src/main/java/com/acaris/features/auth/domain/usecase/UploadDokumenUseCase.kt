package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import java.io.File
import javax.inject.Inject

class UploadDokumenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(documentType: String, semester: Int?, file: File): Result<Unit> {

        if (documentType !in listOf("krs", "khs", "transkrip")) {
            return Result.failure(Exception("Jenis dokumen tidak valid."))
        }

        if (documentType != "transkrip" && semester == null) {
            return Result.failure(Exception("Semester wajib diisi untuk KRS dan KHS."))
        }

        if (!file.exists() || file.length() == 0L) {
            return Result.failure(Exception("File PDF tidak ditemukan atau kosong."))
        }

        // Batasi ukuran file misal maksimal 5 MB (5 * 1024 * 1024 bytes)
        if (file.length() > 1048576) {
            return Result.failure(Exception("Ukuran file maksimal 1 MB."))
        }

        return repository.uploadDokumen(documentType, semester, file)
    }
}