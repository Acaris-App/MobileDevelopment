package com.acaris.features.chatbot.domain.usecase

import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class CloseChatSessionUseCase @Inject constructor(
    private val repository: ChatbotRepository
) {
    suspend operator fun invoke(sessionId: String, finalSummary: String): Result<Unit> {
        return repository.closeSession(sessionId, finalSummary)
    }
}