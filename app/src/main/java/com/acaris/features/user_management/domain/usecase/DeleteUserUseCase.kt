package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(id: String, currentStatus: String): Result<Unit> {
        // 🛡️ Business Rule: Hanya bisa dihapus jika status inactive
        if (currentStatus != "inactive") {
            return Result.failure(Exception("Operasi ditolak! Akun harus dinonaktifkan (inactive) terlebih dahulu sebelum dihapus permanen."))
        }

        return repository.deleteUserPermanently(id)
    }
}