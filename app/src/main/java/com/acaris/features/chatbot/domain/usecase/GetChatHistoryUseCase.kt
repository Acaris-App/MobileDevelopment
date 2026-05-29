package com.acaris.features.chatbot.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val repository: ChatbotRepository
) {
    suspend operator fun invoke(): Result<List<ChatHistoryDomain>> {
        return repository.getChatHistory()
    }
}