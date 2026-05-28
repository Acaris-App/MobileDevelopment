package com.acaris.features.chatbot.domain.usecase

import com.acaris.features.chatbot.domain.model.ChatReplyDomain
import com.acaris.features.chatbot.domain.repository.ChatbotRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val repository: ChatbotRepository
) {
    suspend operator fun invoke(sessionId: String?, message: String): Result<ChatReplyDomain> {
        return repository.sendMessage(sessionId, message)
    }
}