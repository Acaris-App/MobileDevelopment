package com.acaris.features.chatbot.domain.repository

import com.acaris.features.chatbot.domain.model.ChatHistoryDomain
import com.acaris.features.chatbot.domain.model.ChatReplyDomain
import com.acaris.features.chatbot.domain.model.ChatSessionDomain
import com.acaris.features.chatbot.domain.model.ChatSummaryDomain

interface ChatbotRepository {
    suspend fun getActiveSession(): Result<ChatSessionDomain?>

    suspend fun sendMessage(sessionId: String?, message: String): Result<ChatReplyDomain>

    suspend fun generateSummary(sessionId: String): Result<ChatSummaryDomain>

    suspend fun closeSession(sessionId: String, finalSummary: String): Result<Unit>

    suspend fun getChatHistory(): Result<List<ChatHistoryDomain>>

    suspend fun getChatHistoryDetail(sessionId: String): Result<ChatSessionDomain>
}