package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import java.io.File
import javax.inject.Inject

class UpdateDokumenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(documentId: Int, documentType: String, semester: Int?, file: File): Result<Unit> {
        if (!file.exists() || file.length() == 0L) return Result.failure(Exception("File PDF tidak ditemukan atau kosong."))
        if (file.length() > 1048576) return Result.failure(Exception("Ukuran file maksimal 1 MB."))

        return repository.updateDokumen(documentId, documentType, semester, file)
    }
}