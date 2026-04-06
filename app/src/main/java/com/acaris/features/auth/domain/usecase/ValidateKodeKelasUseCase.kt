package com.acaris.features.auth.domain.usecase

import com.acaris.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateKodeKelasUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(kodeKelas: String): Result<Unit> {
        if (kodeKelas.isBlank()) {
            return Result.failure(Exception("Kode kelas tidak boleh kosong."))
        }
        return repository.validateKodeKelas(kodeKelas)
    }
}