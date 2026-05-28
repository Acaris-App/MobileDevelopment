package com.acaris.features.chatbot.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class GetActiveSessionUseCase @Inject constructor(
    private val repository: ChatbotRepository
) {
    suspend operator fun invoke(): Result<ChatSessionDomain?> {
        return repository.getActiveSession()
    }
}