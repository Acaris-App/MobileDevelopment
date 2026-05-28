package com.acaris.features.chatbot.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatSummaryDomain
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class GenerateChatSummaryUseCase @Inject constructor(
    private val repository: ChatbotRepository
) {
    suspend operator fun invoke(sessionId: String): Result<ChatSummaryDomain> {
        return repository.generateSummary(sessionId)
    }
}