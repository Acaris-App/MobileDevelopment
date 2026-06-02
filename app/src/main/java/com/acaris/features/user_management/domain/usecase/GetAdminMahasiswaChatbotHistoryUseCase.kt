package com.acaris.features.user_management.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetAdminMahasiswaChatbotHistoryUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(userId: String): Result<List<ChatHistoryDomain>> {
        return repository.getMahasiswaChatbotHistory(userId)
    }
}