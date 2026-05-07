// File: domain/usecase/GetUserDetailUseCase.kt
package com.acaris.features.user_management.domain.usecase

import com.acaris.features.user_management.domain.model.User // 🌟 IMPOR DOMAIN MODEL
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetUserDetailUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    // 🌟 UBAH KEMBALIAN MENJADI Result<User>
    suspend operator fun invoke(id: String): Result<User> {
        return repository.getUserDetail(id)
    }
}