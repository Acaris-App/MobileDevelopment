package com.acaris.features.user_management.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.user_management.domain.repository.UserManagementRepository
import javax.inject.Inject

class GetAdminMahasiswaChatbotDetailUseCase @Inject constructor(
    private val repository: UserManagementRepository
) {
    suspend operator fun invoke(userId: String, sessionId: String): Result<ChatSessionDomain> {
        return repository.getMahasiswaChatbotDetail(userId, sessionId)
    }
}